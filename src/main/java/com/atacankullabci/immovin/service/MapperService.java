package com.atacankullabci.immovin.service;

import com.atacankullabci.immovin.common.SpotifyUser;
import com.atacankullabci.immovin.common.SpotifyUserImage;
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
        SpotifyUserImage userImage;
        if (userDTO.getImages().length > 0) {
            userImage = new SpotifyUserImage(userDTO.getImages()[0].getHeight(),
                    userDTO.getImages()[0].getWidth(), userDTO.getImages()[0].getUrl());
        } else {
            userImage = new SpotifyUserImage();
        }

        SpotifyUser spotifyUser = new SpotifyUser(userDTO.getDisplay_name(), userDTO.getExternal_urls().getSpotify(),
                userImage);

        return new User(spotifyUser, null, token);
    }
}
