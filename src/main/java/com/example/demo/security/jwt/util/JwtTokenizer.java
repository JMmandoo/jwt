package com.example.demo.security.jwt.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class JwtTokenizer {

  private final byte[] accessSecret;
  private final byte[] refreshSecret;

  public final static Long ACCESS_TOKEN_EXPIRE_COUNT = 30 * 60 * 1000L; // 30 minutes
  public final static Long REFRESH_TOKEN_EXPIRE_COUNT = 7 * 24 * 60 * 60 * 1000L; // 7 days

  public JwtTokenizer(@Value("${jwt.secretKey}") String accessSecret, @Value("${jwt.refreshKey}") String refreshSecret) {
    this.accessSecret = accessSecret.getBytes(StandardCharsets.UTF_8);
    this.refreshSecret = refreshSecret.getBytes(StandardCharsets.UTF_8);
  }

  /**
   * AccessToken 생성
   */
  public String createAccessToken(Long id, String email, List<String> roles) {
    return createToken(id, email, roles, ACCESS_TOKEN_EXPIRE_COUNT, accessSecret);
  }

  /**
   * RefreshToken 생성
   */
  public String createRefreshToken(Long id, String email, List<String> roles) {
    return createToken(id, email, roles, REFRESH_TOKEN_EXPIRE_COUNT, refreshSecret);
  }


  private String createToken(Long id, String email, List<String> roles,
                             Long expire, byte[] secretKey) {
    Claims claims = Jwts.claims().setSubject(email);

    claims.put("roles", roles);
    claims.put("userId", id);

    return Jwts.builder()
        .setClaims(claims)
        .setIssuedAt(new Date())
        .setExpiration(new Date(new Date().getTime() + expire))
        .signWith(getSigningKey(secretKey))
        .compact();
  }

  /**
   * 토큰에서 유저 아이디 얻기
   */
  public Long getUserIdFromToken(String token) {
    String[] tokenArr = token.split(" ");
    token = tokenArr[1];
    Claims claims = parseToken(token, accessSecret);
    return Long.valueOf((Integer)claims.get("userId"));
  }

  public Claims parseAccessToken(String accessToken) {
    return parseToken(accessToken, accessSecret);
  }

  public Claims parseRefreshToken(String refreshToken) {
    return parseToken(refreshToken, refreshSecret);
  }


  public Claims parseToken(String token, byte[] secretKey) {
    return Jwts.parserBuilder()
        .setSigningKey(getSigningKey(secretKey))
        .build()
        .parseClaimsJws(token)
        .getBody();
  }

  /**
   * @param secretKey - byte형식
   * @return Key 형식 시크릿 키
   */
  public static Key getSigningKey(byte[] secretKey) {
    return Keys.hmacShaKeyFor(secretKey);
  }

//    /**
//     * 토큰 검증
//     * @param jwt
//     * @return
//     * @throws UnsupportedEncodingException
//     */
//    public Map<String, Object> checkJwt(String jwt) throws UnsupportedEncodingException {
//        Map<String, Object> claimMap = null;
//        try {
//            Claims claims = Jwts.parserBuilder().build()
//                    .
//                    .setSigningKey(accessSecret) // 키 설정
//                    .parseClaimsJws(jwt) // jwt의 정보를 파싱해서 시그니처 값을 검증한다.
//                    .getBody();
//
//            claimMap = claims;
//
//        } catch (ExpiredJwtException e) { // 토큰이 만료되었을 경우
//            System.out.println(e);
//
//        } catch (Exception e) { // 나머지 에러의 경우
//            System.out.println(e);
//        }
//        return claimMap;
//    }
}