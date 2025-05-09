package corrumptus.anotacoes_video.utils.authentication;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import corrumptus.anotacoes_video.repository.UserRepository;

@Service
public class UserAuthentication implements UserDetailsService {
    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByLogin(username)
            .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }
}
