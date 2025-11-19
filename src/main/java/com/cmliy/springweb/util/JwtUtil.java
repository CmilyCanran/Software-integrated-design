// package: Java包声明，用于组织类和避免命名冲突
package com.cmliy.springweb.util;

// import: 导入其他包中的类，以便在当前类中使用
import io.jsonwebtoken.*;                               // 导入JWT库的所有类
import io.jsonwebtoken.security.Keys;                 // 导入JWT密钥生成工具
import org.springframework.stereotype.Component;          // 导入Spring组件注解

import javax.crypto.SecretKey;                         // 导入Java加密密钥接口
import java.util.Date;                                 // 导入Java日期类
import java.util.function.Function;                    // 导入Java函数式接口

/**
 * 🔑 JWT工具类
 *
 * JWT（JSON Web Token）是一种开放标准（RFC 7519），用于在各方之间安全地传输信息。
 * 这个工具类提供JWT令牌的生成、解析和验证功能。
 *
 * JWT结构：
 * 1. Header（头部）：指定令牌类型和签名算法
 * 2. Payload（载荷）：包含用户信息和元数据
 * 3. Signature（签名）：用于验证令牌完整性
 *
 * @Component: Spring框架注解，标记这是一个组件类，
 *             Spring容器会自动扫描并注册这个类为Bean。
 */
@Component // @Component注解：声明这是一个Spring组件，Spring会自动管理其生命周期
public class JwtUtil { // public class: 定义公共类，其他类可以访问

    // ===== JWT配置常量 =====

    /**
     * 🔐 JWT签名密钥
     * 用于JWT令牌的签名和验证，必须是保密的。
     * 密钥长度至少32字节（256位）以确保HMAC-SHA256算法的安全性。
     */
    private final String secret = "mySecretKey123456789012345678901234567890"; // JWT签名密钥，实际项目中应该从配置文件读取

    /**
     * ⏰ JWT过期时间（毫秒）
     * 86400000毫秒 = 24小时 = 24 * 60 * 60 * 1000
     * 令牌过期后需要重新登录获取新令牌。
     */
    private final long expiration = 86400000; // JWT令牌有效期：24小时

    // ===== 核心方法 =====

    /**
     * 🔑 获取JWT签名密钥
     *
     * 使用密钥字符串生成HMAC-SHA256算法所需的SecretKey对象。
     * 密钥的安全性直接影响JWT令牌的安全性。
     *
     * @return SecretKey: 用于JWT签名的密钥对象
     */
    private SecretKey getSigningKey() { // private方法：只在类内部使用
        // Keys.hmacShaKeyFor(): 根据字节数组生成HMAC-SHA算法的密钥
        return Keys.hmacShaKeyFor(secret.getBytes()); // 将字符串密钥转换为字节数组并生成密钥对象
    }

    /**
     * 📤 从JWT令牌中提取用户名
     *
     * 从JWT的Payload部分提取subject声明，通常存储用户名。
     * 这是一个便捷方法，内部调用extractClaim方法。
     *
     * @param token: JWT令牌字符串
     * @return String: 从令牌中提取的用户名
     */
    public String extractUsername(String token) { // public方法：供其他类调用
        // Claims::getSubject: 方法引用，等同于claims -> claims.getSubject()
        return extractClaim(token, Claims::getSubject); // 调用通用提取方法，提取subject声明
    }

    /**
     * ⏰ 从JWT令牌中提取过期时间
     *
     * 从JWT的Payload部分提取exp声明，表示令牌过期时间。
     * 这是一个便捷方法，内部调用extractClaim方法。
     *
     * @param token: JWT令牌字符串
     * @return Date: 从令牌中提取的过期时间
     */
    public Date extractExpiration(String token) { // public方法：供其他类调用
        // Claims::getExpiration: 方法引用，等同于claims -> claims.getExpiration()
        return extractClaim(token, Claims::getExpiration); // 调用通用提取方法，提取过期时间声明
    }

    /**
     * 🎯 从JWT令牌中提取指定声明
     *
     * 这是一个通用的声明提取方法，可以根据需要提取任何JWT声明。
     * 使用函数式接口，提供灵活的声明提取方式。
     *
     * @param <T>: 泛型类型，表示返回值的类型
     * @param token: JWT令牌字符串
     * @param claimsResolver: 函数式接口，用于从Claims对象中提取特定值
     * @return T: 从令牌中提取的声明值，类型由泛型T决定
     */
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) { // 泛型方法：支持多种返回类型
        final Claims claims = extractAllClaims(token); // 首先提取所有声明
        return claimsResolver.apply(claims); // 应用函数式接口提取特定声明
    }

    /**
     * 📋 从JWT令牌中提取所有声明
     *
     * 解析JWT令牌，提取Header和Payload中的所有声明。
     * 在解析过程中会验证令牌的签名，确保令牌未被篡改。
     *
     * @param token: JWT令牌字符串
     * @return Claims: 包含所有JWT声明的对象
     * @throws JwtException: 当令牌格式错误或签名验证失败时抛出
     */
    private Claims extractAllClaims(String token) { // private方法：只在类内部使用
        // Jwts.parserBuilder(): 创建JWT解析器构建器
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey()) // 设置签名密钥，用于验证令牌签名
                .build() // 构建解析器对象
                .parseClaimsJws(token) // 解析JWT令牌并验证签名
                .getBody(); // 获取Payload部分（Claims对象）
    }

    /**
     * ⏰ 检查JWT令牌是否过期
     *
     * 比较令牌的过期时间与当前时间，判断令牌是否已过期。
     * 过期的令牌应该被拒绝访问。
     *
     * @param token: JWT令牌字符串
     * @return Boolean: true表示已过期，false表示未过期
     */
    private Boolean isTokenExpired(String token) { // private方法：只在类内部使用
        // extractExpiration(): 提取令牌过期时间
        // .before(new Date()): 检查过期时间是否早于当前时间
        return extractExpiration(token).before(new Date()); // 如果过期时间早于当前时间，则令牌已过期
    }

    /**
     * 🔑 生成JWT访问令牌
     *
     * 根据Spring Security用户详情生成JWT令牌。
     * 令牌包含用户名作为subject，用于后续认证。
     *
     * @param userDetails: Spring Security用户详情对象，包含用户信息和权限
     * @return String: 生成的JWT令牌字符串
     */
    public String generateToken(org.springframework.security.core.userdetails.UserDetails userDetails) { // public方法：供其他类调用
        // userDetails.getUsername(): 获取用户名作为JWT的subject
        return createToken(userDetails.getUsername(), expiration); // 调用令牌创建方法
    }

    /**
     * 🔧 创建JWT令牌
     *
     * 使用JWT构建器模式创建令牌，设置标准声明和签名。
     * 生成的令牌包含签发时间、过期时间和用户信息。
     *
     * @param subject: JWT主题，通常是用户名
     * @param expiration: 令牌过期时间（毫秒）
     * @return String: 生成的JWT令牌字符串
     */
    private String createToken(String subject, long expiration) { // private方法：只在类内部使用
        Date now = new Date(); // 当前时间：签发时间
        Date expiryDate = new Date(now.getTime() + expiration); // 过期时间：当前时间 + 有效期

        // Jwts.builder(): 创建JWT构建器
        return Jwts.builder()
                .setSubject(subject)           // 设置subject声明（用户名）
                .setIssuedAt(now)              // 设置签发时间声明
                .setExpiration(expiryDate)     // 设置过期时间声明
                .signWith(getSigningKey(), SignatureAlgorithm.HS256) // 使用HMAC-SHA256算法签名
                .compact(); // .compact(): 生成紧凑的JWT字符串
    }

    /**
     * ✅ 验证JWT令牌有效性
     *
     * 验证令牌的完整性、过期时间和用户名匹配性。
     * 只有通过所有验证的令牌才被认为是有效的。
     *
     * @param token: JWT令牌字符串
     * @param userDetails: Spring Security用户详情对象，用于验证用户名匹配
     * @return Boolean: true表示令牌有效，false表示令牌无效
     */
    public Boolean validateToken(String token, org.springframework.security.core.userdetails.UserDetails userDetails) { // public方法：供其他类调用
        try { // try-catch: 捕获令牌解析和验证过程中的异常
            // extractUsername(): 从令牌中提取用户名
            final String username = extractUsername(token); // 提取令牌中的用户名
            // 用户名匹配检查 + 过期时间检查
            return (username.equals(userDetails.getUsername()) && !isTokenExpired(token)); // 验证用户名匹配且未过期
        } catch (JwtException | IllegalArgumentException e) { // 捕获JWT相关异常
            return false; // 任何异常都表示令牌无效
        }
    }

    /**
     * 🕐 获取JWT令牌过期时间配置
     *
     * 返回配置的令牌过期时间，供外部使用（如设置响应头）。
     *
     * @return long: 令牌过期时间（毫秒）
     */
    public long getExpiration() { // public方法：供其他类调用
        return expiration; // 返回配置的过期时间
    }
}