package pac.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import pac.daoInter.AccountDAO;
import pac.entities.Account;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by macbookair on 02.04.16.
 */
@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private AccountDAO accountDAO;
    
//    @Autowired
//    private AccountTypeService accountTypeService;

    @Override
    public UserDetails loadUserByUsername(String login) throws UsernameNotFoundException {
        Account account = accountDAO.findOne(login);
        System.out.println("------ "+login);
        Set<GrantedAuthority> roles = new HashSet<>();
        roles.add(new SimpleGrantedAuthority(account.getAccountType().getTypeName()));

        UserDetails userDetails =
                new org.springframework.security.core.userdetails.User(account.getLogin(),
                        account.getPass(),
                        roles);

        return userDetails;
    }

}
