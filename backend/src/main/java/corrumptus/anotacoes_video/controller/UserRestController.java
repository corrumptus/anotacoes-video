package corrumptus.anotacoes_video.controller;

import java.net.URI;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import corrumptus.anotacoes_video.dto.user.UserLoginDTO;
import corrumptus.anotacoes_video.dto.user.NewUserDTO;
import corrumptus.anotacoes_video.entity.User;
import corrumptus.anotacoes_video.mapper.UserMapper;
import corrumptus.anotacoes_video.repository.UserRepository;
import corrumptus.anotacoes_video.utils.authentication.TokenJWTResponse;
import corrumptus.anotacoes_video.utils.authentication.UserTokenJWT;
import jakarta.persistence.EntityExistsException;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/user")
public class UserRestController {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AuthenticationManager manager;

    @Autowired
    private UserTokenJWT tokenService;

    private String PROFILE_PIC_FOLDER = "profile-pics";

    private int MAX_SIZE = 100 * 1024 * 1024;

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

    @PostMapping("/signup")
    public ResponseEntity<TokenJWTResponse> signup(
        @RequestBody @Valid NewUserDTO request,
        UriComponentsBuilder uriBuilder
    ) throws Exception {
        if (userRepository.findByLogin(request.login()).isPresent())
            throw new EntityExistsException("This user already exists");

        if (!request.profilePic().isEmpty()) {
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

        String profilePicFileName = request.login() + "-" + UUID.randomUUID();
        Path profilePicPath = Paths.get(PROFILE_PIC_FOLDER, profilePicFileName);
        request.profilePic().transferTo(profilePicPath.toFile());

        User newUser = userRepository.save(UserMapper.toEntity(request, profilePicFileName));
        URI uri = uriBuilder.path("/video/{id}").buildAndExpand(newUser.getId()).toUri();

        String tokenJWT = tokenService.newToken(newUser);

        return ResponseEntity
            .created(uri)
            .body(new TokenJWTResponse(tokenJWT));
    }
}
