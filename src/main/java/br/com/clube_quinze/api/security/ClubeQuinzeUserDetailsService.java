package br.com.clube_quinze.api.security;

import br.com.clube_quinze.api.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class ClubeQuinzeUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public ClubeQuinzeUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByEmail(username)
                .map(ClubeQuinzeUserDetails::from)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado"));
    }
}
