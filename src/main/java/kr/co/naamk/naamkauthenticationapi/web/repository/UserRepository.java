package kr.co.naamk.naamkauthenticationapi.web.repository;

import kr.co.naamk.naamkauthenticationapi.domain.common.TbUsers;
import kr.co.naamk.naamkauthenticationapi.web.dto.UserDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository< TbUsers, Long > {
    @Query(
            nativeQuery = true,
            value = """
            SELECT
              u.id,
              u.name,
              u.nickname,
              u.email,
              u.role,
              COALESCE(ph.is_active, true) AS penalty,
              u.created_at AS createdAt
            FROM public.users u
            LEFT JOIN (
              SELECT DISTINCT ON (linked_id) *
              FROM community.penalty_hist
              WHERE type = 'user'
              ORDER BY linked_id, created_at DESC
              LIMIT 1
            ) ph ON ph.linked_id = u.id
            WHERE (:name IS NULL OR u.name LIKE CONCAT(:name, '%'))
              AND (:nickname IS NULL OR u.nickname LIKE CONCAT(:nickname, '%'))
              AND (:email IS NULL OR u.email LIKE CONCAT(:email, '%'))
              AND (:userStatus IS NULL OR u.role LIKE CONCAT(:userStatus, '%'))
              AND (:penaltyStatus IS NULL OR COALESCE(ph.is_active, true) = CAST(:penaltyStatus AS boolean))
              AND (CAST(:startDate AS timestamp) IS NULL OR u.created_at >= CAST(:startDate AS TIMESTAMP))
              AND (CAST(:endDate AS timestamp) IS NULL OR u.created_at < CAST(:endDate AS TIMESTAMP))
            ORDER BY u.created_at DESC
        """,
            countQuery = """
            SELECT COUNT(*)
            FROM public.users u
            LEFT JOIN (
              SELECT is_active, linked_id
              FROM community.penalty_hist
              WHERE type = 'user'
              ORDER BY linked_id, created_at DESC
              LIMIT 1
            ) ph ON ph.linked_id = u.id
            WHERE (:name IS NULL OR u.name LIKE CONCAT(:name, '%'))
              AND (:nickname IS NULL OR u.nickname LIKE CONCAT(:nickname, '%'))
              AND (:email IS NULL OR u.email LIKE CONCAT(:email, '%'))
              AND (:userStatus IS NULL OR u.role LIKE CONCAT(:userStatus, '%'))
              AND (:penaltyStatus IS NULL OR COALESCE(ph.is_active, true) = CAST(:penaltyStatus AS boolean))
              AND (CAST(:startDate AS timestamp) IS NULL OR u.created_at >= CAST(:startDate AS TIMESTAMP))
              AND (CAST(:endDate AS timestamp) IS NULL OR u.created_at < CAST(:endDate AS TIMESTAMP))
        """
    )
    Page<UserDto> findUsersWithPenalty(
            @Param("name") String name,
            @Param("nickname") String nickname,
            @Param("userStatus") String userStatus,
            @Param("email") String email,
            @Param("penaltyStatus") Boolean penaltyStatus,
            @Param("startDate") Timestamp startDate,
            @Param("endDate") Timestamp endDate,
            Pageable pageable
    );

    @Query(nativeQuery = true, value = """
            SELECT
                u.id,
                u.name,
                u.nickname,
                u.email,
                u.intro,
                u.role,
                u.created_at as createdAt,
                f.thumb_s_url,
                COALESCE(ph.is_active, true) AS penalty
            FROM public.users u
            LEFT JOIN (
                SELECT linked_id, is_active
                FROM community.penalty_hist
                WHERE type = 'user'
                ORDER BY linked_id, created_at DESC
              LIMIT 1
            ) ph ON ph.linked_id = u.id
            LEFT JOIN (
                SELECT linked_id, thumb_s_url
                FROM public.files
                WHERE type = 'profile'
            ) f on f.linked_id = u.id
            WHERE u.id = :userId
            """)
    Optional<UserDto.UserDetailResponse> findUserById(@Param( "userId" ) Long userId);

}
