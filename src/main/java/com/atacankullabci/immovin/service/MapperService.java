package com.atacankullabci.immovin.service;

import com.atacankullabci.immovin.common.Token;
import com.atacankullabci.immovin.common.User;
import com.atacankullabci.immovin.dto.TokenDTO;
import com.atacankullabci.immovin.dto.UserDTO;
import org.springframework.stereotype.Service;

@Service
public class MapperService {

    public Token mapToken(TokenDTO tokenDTO) {
        return new Token(tokenDTO.getAccess_token(), tokenDTO.getExpires_in(), tokenDTO.getRefresh_token());
    }

    public User mapUser(UserDTO userDTO, Token token) {
        return new User(userDTO.getDisplay_name(), userDTO.getExternal_urls().getSpotify(), null, token);
    }
}
