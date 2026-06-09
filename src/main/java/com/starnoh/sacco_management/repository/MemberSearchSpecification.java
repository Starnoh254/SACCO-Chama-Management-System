package com.starnoh.sacco_management.repository;

import com.starnoh.sacco_management.dto.MemberSearchRequest;
import com.starnoh.sacco_management.entity.Members;
import com.starnoh.sacco_management.entity.Users;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class MemberSearchSpecification {

    public static Specification<Members> build(MemberSearchRequest req) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            // JOIN users table once — shared by all predicates below
            Join<Members, Users> user = root.join("user", JoinType.INNER);
            // Keyword: OR across firstName, lastName,
            // membershipNumber, nationalId

            if (hasValue(req.getKeyword())) {
                String like = like(req.getKeyword());
                predicates.add(cb.or(
                        cb.like(cb.lower(user.get("firstName")), like),
                        cb.like(cb.lower(user.get("lastName")), like),
                        cb.like(cb.lower(root.get("membershipNumber")), like),
                        cb.like(cb.lower(root.get("nationalId")), like)
                ));
            }
            // Individual field filters (all AND)
            if (hasValue(req.getFirstName()))
                predicates.add(cb.like(cb.lower(user.get("firstName")),
                        like(req.getFirstName())));
            if (hasValue(req.getLastName()))
                predicates.add(cb.like(cb.lower(user.get("lastName")),
                        like(req.getLastName())));
            if (hasValue(req.getMembershipNumber()))
                predicates.add(cb.like(cb.lower(root.get("membershipNumber")),
                        like(req.getMembershipNumber())));
            if (hasValue(req.getNationalId()))
                predicates.add(cb.equal(root.get("nationalId"),
                        req.getNationalId()));
            if (hasValue(req.getStatus()))
                predicates.add(cb.equal(cb.upper(root.get("status")),
                        req.getStatus().toUpperCase()));
            if (hasValue(req.getAddress()))
                predicates.add(cb.like(cb.lower(root.get("address")),
                        like(req.getAddress())));

            // Date range filters
            if (req.getDateJoinedFrom() != null)
                predicates.add(cb.greaterThanOrEqualTo(
                        root.get("dateJoined"), req.getDateJoinedFrom()));

            if (req.getDateJoinedTo() != null)
                predicates.add(cb.lessThanOrEqualTo(
                        root.get("dateJoined"), req.getDateJoinedTo()));
            return cb.and(predicates.toArray(new Predicate[0]));
        };


    }


    private static boolean hasValue(String s) {
        return s != null && !s.isBlank();
    }
    private static String like(String value) {
        return "%" + value.toLowerCase() + "%";
    }

}



