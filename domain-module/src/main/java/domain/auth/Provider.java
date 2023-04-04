package domain.auth;

public enum Provider {

    GOOGLE("구글"),
    NAVER("네이버"),
    KAKAO("카카오"),
    APPLE("애플");

    private final String provider;

    Provider(String provider) { this.provider = provider; }

    public String getProvider() { return provider; }
    
}
