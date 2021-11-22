package cn.isekai.keycloak.avatar.jpa;

import lombok.*;
import org.keycloak.connections.jpa.JpaConnectionProvider;
import org.keycloak.models.KeycloakSession;

import javax.persistence.*;
import java.util.Date;
import java.util.Objects;

@Getter
@Setter
@ToString
@RequiredArgsConstructor
@Entity
@Table(name = "USER_AVATAR")
@NamedQueries({
        @NamedQuery(
                name = "getAvatarByUser",
                query = "SELECT a FROM UserAvatarEntity a WHERE a.userId=:user_id"
        )
})
public class UserAvatarEntity {
    @Id
    @Column(name = "USER_ID")
    private String userId;

    @Column(name = "AVATAR_ID")
    private String avatarId;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "UPDATE_AT", nullable = false)
    private Date updateAt;

    @Column(name = "STORAGE")
    private String storage;

    @Column(name = "FALLBACK_URL")
    private String fallbackURL;

    @Column(name = "E_TAG")
    private String eTag;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        if (!(o instanceof UserAvatarEntity)) return false;
        UserAvatarEntity that = (UserAvatarEntity) o;

        return Objects.equals(userId, that.userId);
    }

    @Override
    public int hashCode() {
        return userId.hashCode();
    }

    private static EntityManager getEntityManager(KeycloakSession session) {
        return session.getProvider(JpaConnectionProvider.class).getEntityManager();
    }

    public static UserAvatarEntity findByUserId(KeycloakSession session, String userId) {
        try {
            return getEntityManager(session)
                    .createNamedQuery("getAvatarByUser", UserAvatarEntity.class)
                    .setParameter("user_id", userId)
                    .getSingleResult();
        } catch (NoResultException ignored) {
            return null;
        }
    }

    public static boolean save(KeycloakSession session, UserAvatarEntity entity) {
        EntityManager em = getEntityManager(session);

        if (findByUserId(session, entity.getUserId()) == null) { // insert
            em.persist(entity);
        } else { // update
            em.merge(entity);
        }

        return true;
    }
}
