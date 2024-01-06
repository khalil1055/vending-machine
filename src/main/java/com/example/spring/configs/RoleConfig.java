package com.example.spring.configs;

import com.example.spring.entities.Privilege;
import com.example.spring.entities.Role;
import com.example.spring.enums.Roles;
import com.example.spring.repositories.PrivilegeRepository;
import com.example.spring.enums.Privileges;
import com.example.spring.repositories.RoleRepository;
import lombok.AllArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Configuration
@AllArgsConstructor
public class RoleConfig {
    private final RoleRepository roleRepository;
    private final PrivilegeRepository privilegeRepository;

    @Bean
    CommandLineRunner roleSeeder() {
        return args -> {
            Role seller = new Role(Roles.SELLER.toString());
            Collection<Privilege> sellerPrivileges = new ArrayList<>();
            sellerPrivileges.add(new Privilege(Privileges.PRODUCT_ADD.toString()));
            sellerPrivileges.add(new Privilege(Privileges.PRODUCT_REMOVE.toString()));
            sellerPrivileges.add(new Privilege(Privileges.PRODUCT_UPDATE.toString()));

            Role buyer = new Role(Roles.BUYER.toString());
            Collection<Privilege> buyerPrivileges = new ArrayList<>();
            buyerPrivileges.add(new Privilege(Privileges.PRODUCT_BUY.toString()));
            buyerPrivileges.add(new Privilege(Privileges.USER_DEPOSIT.toString()));

            privilegeRepository.saveAll(sellerPrivileges);
            privilegeRepository.saveAll(buyerPrivileges);

            seller.setPrivileges(sellerPrivileges);
            buyer.setPrivileges(buyerPrivileges);

            roleRepository.saveAll(List.of(buyer, seller));
        };
    }
}
