package com.vk.dwzkf.tglib.botcore.context.photos;

import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.bots.DefaultAbsSender;
import org.telegram.telegrambots.meta.api.methods.GetFile;
import org.telegram.telegrambots.meta.api.objects.File;
import org.telegram.telegrambots.meta.api.objects.PhotoSize;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.List;
import java.util.Objects;

/**
 * @author Roman Shageev
 * @since 30.11.2023
 */
@Slf4j
public class DefaultMessagePhoto<T extends DefaultAbsSender> extends MessagePhoto {
    private final T botRunner;

    public DefaultMessagePhoto(T botRunner, List<PhotoSize> variants) {
        super(variants);
        this.botRunner = botRunner;
    }

    @Override
    protected BufferedImage download0(PhotoSize photoSize) throws DownloadFailedException {
        Objects.requireNonNull(photoSize);
        String fileId = photoSize.getFileId();
        try {
            File fileData = botRunner.execute(new GetFile(fileId));
            java.io.File file = botRunner.downloadFile(fileData.getFilePath());
            return ImageIO.read(file);
        } catch (IOException | TelegramApiException e) {
            log.warn("Unable to download photo. {}", e.getMessage());
            String errorMessage = String.format("Download exception for file: %s", photoSize.getFileId());
            if (log.isDebugEnabled()) {
                log.warn(errorMessage, e);
            }
            throw new DownloadFailedException(errorMessage, e);
        }
    }
}
