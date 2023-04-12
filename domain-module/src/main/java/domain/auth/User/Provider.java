package domain.auth.User;

public enum Provider {

    GOOGLE("GOOGLE"),
    NAVER("NAVER"),
    KAKAO("KAKAO"),
    APPLE("APPLE");

    private final String provider;

    Provider(String provider) { this.provider = provider; }

    public String getProvider() { return provider; }
    
}
