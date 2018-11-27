package ru.configuration.authentication;

import lombok.Getter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.SpringSecurityCoreVersion;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import ru.constant.ReviewStatus;
import ru.constant.UserRole;
import ru.dao.entity.*;
import ru.service.UserInfoService;

import java.util.Collection;
import java.util.List;
import java.util.Map;
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

    private final UserInfoService userInfoService;


    @Getter
    private List<Map<String,String>> pendingOrders;

    @Getter
    private List<RouteReview> routeReviews;

    @Getter
    private List<RouteReviewOpinion> opinions;

    @Getter
    private Long pendingOpinions;

    @Getter
    private List<Map<String,String>> managedOffers;

    @Getter
    private List<Map<String,String>> receivedContracts;

    @Getter
    private List<Map<String,String>> sentContracts;

    @Getter
    private Point companyPoint;





    // ~ Constructors
    // ===================================================================================================


    /**
     * This constructor should only be used by <code>AuthenticationManager</code> or
     * <code>AuthenticationProvider</code> implementations that are satisfied with
     * producing a trusted (i.e. {@link #isAuthenticated()} = <code>true</code>)
     * authentication token.
     *  @param user
     * @param credentials
     * @param authorities
     * @param userInfoService
     */
    public AuthToken(User user, Object credentials, Collection<? extends GrantedAuthority> authorities, UserInfoService userInfoService) {
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
        this.userInfoService = userInfoService;
        this.refreshAll();
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

    //TODO: get rid of this
    public static AuthToken getCurrentAuthToken(){
        SecurityContext securityContext = SecurityContextHolder.getContext();
        return (AuthToken)securityContext.getAuthentication();
    }

    public void refreshPendingOrders(){
        this.pendingOrders = userInfoService.getPendingOrders(this.companyId);
    }

    public void refreshOpinions(){
        this.opinions = userInfoService.getOpinions(this.companyId);
        this.pendingOpinions = this.opinions.stream().filter(x->x.getReview().getStatus().equals(ReviewStatus.CREATED)).count();
    }

    public void refreshPoint(){
        this.companyPoint = userInfoService.getCompanyPoint(this.companyId);
    }

    public void refreshReceivedContracts(){
        this.receivedContracts = userInfoService.getReceivedContracts(this.companyId);
    }

    public void refreshManagedOffers(){
        this.managedOffers = userInfoService.getManagedOffers(this.companyId);
    }

    public void refreshSentContracts(){
        this.sentContracts = userInfoService.getSentContracts(this.companyId);
    }

    public void refreshReviews(){
        this.routeReviews = userInfoService.getReviews(this.companyId);
    }

    public void refreshAll(){
        if(this.getUser().getUserRole().equals(UserRole.ROLE_TRANSPORT_COMPANY)){
            this.refreshPendingOrders();
            this.refreshOpinions();
            this.refreshReceivedContracts();
            this.refreshPoint();
        } else if (this.getUser().getUserRole().equals(UserRole.ROLE_DISPATCHER)){
            this.refreshManagedOffers();
            this.refreshReviews();
            this.refreshSentContracts();
            this.refreshPoint();
        }
    }

}
