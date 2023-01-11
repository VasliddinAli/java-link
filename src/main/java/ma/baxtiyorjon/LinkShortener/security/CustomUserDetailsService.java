package ma.baxtiyorjon.LinkShortener.security;

import ma.baxtiyorjon.LinkShortener.models.UsersEntity;
import ma.baxtiyorjon.LinkShortener.repositories.UsersRepository;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.stream.Collectors;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UsersRepository usersRepository;

    public CustomUserDetailsService(UsersRepository usersRepository) {
        this.usersRepository = usersRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        final UsersEntity usersEntity = usersRepository.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException("Not found"));
        final Collection<GrantedAuthority> roles = usersEntity.getRoles().stream().map(role -> new SimpleGrantedAuthority(role.getName())).collect(Collectors.toList());
        return new User(usersEntity.getUsername(), usersEntity.getPassword(), roles);
    }
}
