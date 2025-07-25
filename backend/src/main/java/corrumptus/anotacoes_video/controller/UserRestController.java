package corrumptus.anotacoes_video.controller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;

import corrumptus.anotacoes_video.dto.user.UserLoginDTO;
import corrumptus.anotacoes_video.dto.user.FindUserDTO;
import corrumptus.anotacoes_video.dto.user.NewUserDTO;
import corrumptus.anotacoes_video.entity.User;
import corrumptus.anotacoes_video.mapper.UserMapper;
import corrumptus.anotacoes_video.repository.UserRepository;
import corrumptus.anotacoes_video.utils.authentication.TokenJWTResponse;
import corrumptus.anotacoes_video.utils.authentication.UserTokenJWT;

@RestController
@RequestMapping("/user")
public class UserRestController {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AuthenticationManager manager;

    @Autowired
    private UserTokenJWT tokenService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private static final String PROFILE_PIC_FOLDER = "profile-pics";

    private static final long MAX_SIZE = 5l * 1024l * 1024l;

    public UserRestController() throws IOException {
        Path folder = Paths.get(PROFILE_PIC_FOLDER);
        if (!Files.exists(folder)) {
            Files.createDirectories(folder);
        }
    }

    @GetMapping
    public ResponseEntity<List<FindUserDTO>> findUser(@RequestParam("sub") String subLogin) {
        List<UserDetails> usersWithSubLogin = userRepository.findBySubLogin(subLogin);

        return ResponseEntity.ok(
            usersWithSubLogin.stream()
                .map(u -> new FindUserDTO(((User) u).getId(), ((User) u).getLogin()))
                .toList()
        );
    }

    @PostMapping("/login")
    public ResponseEntity<TokenJWTResponse> login(@RequestBody @Valid UserLoginDTO login) {
        UsernamePasswordAuthenticationToken token =
            new UsernamePasswordAuthenticationToken(login.login(), login.password());

        Authentication authentication = manager.authenticate(token);

        User user = (User) authentication.getPrincipal();

        String tokenJWT = tokenService.newToken(user);

        return ResponseEntity
            .ok(new TokenJWTResponse(tokenJWT));
    }

    @PostMapping(value = "/signup", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<TokenJWTResponse> signup(
        @ModelAttribute @Valid NewUserDTO request
    ) throws IllegalStateException, IOException {
        if (userRepository.findByLogin(request.login()).isPresent())
            throw new EntityExistsException("This user already exists");

        if (request.profilePic() != null) {
            if (
                request.profilePic().getContentType() != null
                &&
                (
                    request.profilePic().getContentType() == "image/png"
                    ||
                    request.profilePic().getContentType() == "image/jpeg"
                )
            )
                throw new IllegalArgumentException("Profile picture must be of type png or jpg");
    
            if (request.profilePic().getSize() > MAX_SIZE)
                throw new IllegalArgumentException("Video is bigger than 10MB");
        }

        NewUserDTO encodedPasswordNewUser = UserMapper.encodePassword(
            request,
            passwordEncoder.encode(request.password())
        );

        String fileName = null;
        String imageType = null;

        if (request.profilePic() != null) {
            imageType = request.profilePic().getContentType();
            fileName = request.login() + "-" + UUID.randomUUID() + getExtension(imageType);

            Files.copy(
                request.profilePic().getInputStream(),
                Paths.get(PROFILE_PIC_FOLDER, fileName),
                StandardCopyOption.REPLACE_EXISTING
            );
        }

        User newUser = userRepository.save(
            UserMapper.toEntity(
                encodedPasswordNewUser,
                fileName,
                imageType
            )
        );

        String tokenJWT = tokenService.newToken(newUser);

        return ResponseEntity
            .ok(new TokenJWTResponse(tokenJWT));
    }

    @GetMapping("/name")
    public ResponseEntity<Map<String, String>> getSelfName(
        @AuthenticationPrincipal User user
    ) {
        Map<String, String> response = new HashMap<String, String>();

        response.put("name", user.getLogin());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{login}/profilePic")
    public ResponseEntity<Resource> getUserProfilePic(
        @PathVariable("login") String login
    ) {
        User user = (User) userRepository.findByLogin(login)
            .orElseThrow(() -> new EntityNotFoundException("User doesn't exist"));

        Resource pic = new FileSystemResource(
            Paths.get(PROFILE_PIC_FOLDER, user.getProfilePicPath()).toFile()
        );

        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_TYPE, user.getProfilePicType())
            .body(pic);
    }

    private String getExtension(String fileType) {
        return switch (fileType) {
            case "image/png" -> ".png";
            case "image/jpeg" -> ".jpg";
            default -> throw new IllegalArgumentException("Suported file types: image/png or image/jpeg");
        };
    }
}
