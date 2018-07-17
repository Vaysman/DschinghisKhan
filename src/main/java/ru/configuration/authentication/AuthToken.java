package ru.configuration.authentication;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.SpringSecurityCoreVersion;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import ru.dao.entity.Company;
import ru.dao.entity.User;

import java.util.Collection;
import java.util.Optional;

public class AuthToken extends AbstractAuthenticationToken {

    private static final long serialVersionUID = SpringSecurityCoreVersion.SERIAL_VERSION_UID;

    // ~ Instance fields
    // ================================================================================================

    private final Object principal;
    private Object credentials;
    private String role;
    private String name;
    private String roleName;
    private User user;
    private Integer companyId;

    // ~ Constructors
    // ===================================================================================================

    /**
     * This constructor can be safely used by any code that wishes to create a
     * <code>UsernamePasswordAuthenticationToken</code>, as the {@link #isAuthenticated()}
     * will return <code>false</code>.
     *
     */
    public AuthToken(Object principal, User user, Object credentials) {
        super(null);
        this.principal = principal;
        this.credentials = credentials;
        this.user = user;
        setAuthenticated(false);
    }

    /**
     * This constructor should only be used by <code>AuthenticationManager</code> or
     * <code>AuthenticationProvider</code> implementations that are satisfied with
     * producing a trusted (i.e. {@link #isAuthenticated()} = <code>true</code>)
     * authentication token.
     *
     * @param user
     * @param credentials
     * @param authorities
     */
    public AuthToken(User user, Object credentials, Collection<? extends GrantedAuthority> authorities) {
        super(authorities);
        this.principal = user.getLogin();
        this.credentials = credentials;
        this.name = user.getUsername();
        this.role = user.getUserRole().name();
        this.roleName = user.getUserRole().getRoleName();
        this.user = user;
        this.companyId = Optional
                .ofNullable(user.getCompany())
                .map(Company::getId)
                .orElse(null);
        super.setAuthenticated(true); // must use super, as we override
    }

    // ~ Methods
    // ========================================================================================================

    public Object getCredentials() {
        return this.credentials;
    }

    public User getUser() {
        return user;
    }

    public Object getPrincipal() {
        return this.principal;
    }

    public String getRole() {
        return role;
    }

    @Override
    public String getName() {
        return name;
    }

    public String getRoleName() {
        return roleName;
    }

    public Integer getCompanyId() {
        return companyId;
    }

    public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {
        if (isAuthenticated) {
            throw new IllegalArgumentException(
                    "Cannot set this token to trusted - use constructor which takes a GrantedAuthority list instead");
        }

        super.setAuthenticated(false);
    }

    @Override
    public void eraseCredentials() {
        super.eraseCredentials();
        credentials = null;
    }


    public static AuthToken getCurrentAuthToken(){
        SecurityContext securityContext = SecurityContextHolder.getContext();
        return (AuthToken)securityContext.getAuthentication();
    }
}
