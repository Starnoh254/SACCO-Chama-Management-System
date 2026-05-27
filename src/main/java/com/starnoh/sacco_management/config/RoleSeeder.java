package com.starnoh.sacco_management.config;

import com.starnoh.sacco_management.entity.Roles;
import com.starnoh.sacco_management.repository.RolesRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RoleSeeder {
    @Bean
    CommandLineRunner seedRoles(
            RolesRepository rolesRepository
    ) {
        return args -> {

            createRoleIfNotExists(
                    rolesRepository,
                    "ADMINISTRATOR",
                    "System administrator"
            );

            createRoleIfNotExists(
                    rolesRepository,
                    "TREASURER",
                    "Manages SACCO finances and transactions"
            );

            createRoleIfNotExists(
                    rolesRepository,
                    "MEMBER",
                    "Approved SACCO member"
            );

            createRoleIfNotExists(
                    rolesRepository,
                    "AUDITOR",
                    "Reviews SACCO financial records and reports"
            );

            createRoleIfNotExists(
                    rolesRepository,
                    "USER",
                    "Registered account holder awaiting SACCO membership approval"
            );
        };
    }

    private void createRoleIfNotExists(
            RolesRepository repo,
            String name,
            String description
    ) {

        if (!repo.existsByName(name)) {

            Roles role = new Roles();

            role.setName(name);
            role.setDescription(description);

            repo.save(role);
        }
    }
}
