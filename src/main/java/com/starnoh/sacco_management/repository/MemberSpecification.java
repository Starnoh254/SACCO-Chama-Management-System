package com.starnoh.sacco_management.repository;

import com.starnoh.sacco_management.dto.MemberFilterRequest;
import com.starnoh.sacco_management.entity.Members;
import com.starnoh.sacco_management.entity.Users;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

// This class provides a dynamic Specification for filtering Members based on the criteria defined in MemberFilterRequest.
public class MemberSpecification {


        public static Specification<Members> build(MemberFilterRequest f) {
            return (root, query, cb) -> {
                List<Predicate> predicates = new ArrayList<>();

                // JOIN to users for name / email access
                Join<Members, Users> user = root.join("user", JoinType.INNER);

                // Filter by status
                if (f.getStatus() != null && !f.getStatus().isBlank()) {
                    predicates.add(cb.equal(
                            cb.upper(root.get("status")),
                            f.getStatus().toUpperCase()));
                }

                // Full-text search
                if (f.getSearch() != null && !f.getSearch().isBlank()) {
                    String like = "%" + f.getSearch().toLowerCase() + "%";
                    predicates.add(cb.or(
                            cb.like(cb.lower(user.get("firstName")), like),
                            cb.like(cb.lower(user.get("lastName")),  like),
                            cb.like(cb.lower(root.get("membershipNumber")), like),
                            cb.like(cb.lower(root.get("nationalId")), like)
                    ));
                }

                return cb.and(predicates.toArray(new Predicate[0]));
            };
        }


}
