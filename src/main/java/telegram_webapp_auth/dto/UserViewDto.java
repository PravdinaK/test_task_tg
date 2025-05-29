package telegram_webapp_auth.dto;

public record UserViewDto(
        Long id,
        String firstName,
        String lastName,
        String username,
        String languageCode,
        String photoUrl,
        Boolean allowsWriteToPm
) {}