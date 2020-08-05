package shiro;

import com.zking.model.SysUser;
import com.zking.service.ISysUserService;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.realm.Realm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.util.ByteSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.rmi.activation.UnknownObjectException;
import java.util.Set;

@Component
public class ShiroRealm extends AuthorizingRealm {

    @Autowired
    private ISysUserService sysUserService;

    //      AuthorizationInfo：授权信息
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {
        String username = principalCollection.getPrimaryPrincipal().toString();
        //得到用户角色名
        Set<String> role = sysUserService.findRole(username);
        //得到用户权限名
        Set<String> permission = sysUserService.findPermission(username);
        //设置安全数据库中关于角色权限的内容
        SimpleAuthorizationInfo simpleAuthorizationInfo = new SimpleAuthorizationInfo();
        simpleAuthorizationInfo.setRoles(role);
        simpleAuthorizationInfo.setStringPermissions(permission);

        return simpleAuthorizationInfo;
    }

    //  AuthenticationInfo：认证信息
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken) throws AuthenticationException {
        //getPrincipal  用户账户
        String username = authenticationToken.getPrincipal().toString();
        //getCredentials 用户密码
        String password = authenticationToken.getCredentials().toString();
        //根据用户账号到数据库找到对应的用户信息
        SysUser sysUser = sysUserService.userLogin(username);
        //判断用户是否存在
        if (null == sysUser) {
            throw new RuntimeException("用户不存在");
        }
        //将数据库交给shiro进行匹配
        SimpleAuthenticationInfo simpleAuthenticationInfo = new SimpleAuthenticationInfo(
                sysUser.getUsername(),
                sysUser.getPassword(),
                ByteSource.Util.bytes(sysUser.getSalt()),
                this.getName()
        );
        return simpleAuthenticationInfo;
    }
}
