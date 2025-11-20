package com.emma.emmamusic.domain.favoritos.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AgregarFavoritoRequest {
    private Long usuarioId;
    private String idVideoYoutube;
}

