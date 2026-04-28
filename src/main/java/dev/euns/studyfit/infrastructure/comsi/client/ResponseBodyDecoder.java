package dev.euns.studyfit.infrastructure.comsi.client;

import dev.euns.studyfit.global.exception.BaseException;
import dev.euns.studyfit.infrastructure.comsi.exception.ComsiErrorCode;

import java.nio.ByteBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CodingErrorAction;
import java.nio.charset.StandardCharsets;

final class ResponseBodyDecoder {

    private static final Charset EUC_KR = Charset.forName("EUC-KR");
    private static final Charset[] CHARSETS = {StandardCharsets.UTF_8, EUC_KR};

    private ResponseBodyDecoder() {}

    static String decode(byte[] body) {
        for (Charset charset : CHARSETS) {
            String decoded = decodeWith(body, charset);
            if (decoded != null) {
                return keepJsonOnly(decoded);
            }
        }

        throw new BaseException(ComsiErrorCode.DECODE_ERROR);
    }

    private static String decodeWith(byte[] body, Charset charset) {
        try {
            return charset.newDecoder()
                    .onMalformedInput(CodingErrorAction.REPORT)
                    .onUnmappableCharacter(CodingErrorAction.REPORT)
                    .decode(ByteBuffer.wrap(body))
                    .toString();
        } catch (CharacterCodingException e) {
            return null;
        }
    }

    private static String keepJsonOnly(String text) {
        int lastBrace = text.lastIndexOf('}');
        if (lastBrace < 0) {
            throw new BaseException(ComsiErrorCode.MALFORMED_RESPONSE);
        }

        return text.substring(0, lastBrace + 1);
    }
}
