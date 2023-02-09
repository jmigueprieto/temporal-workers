package me.mprieto.temporal.model.email;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class EmailRequest {
    private String from;
    @NonNull
    private String to;
    @NonNull
    private String subject;
    @NonNull
    private String text;
}
