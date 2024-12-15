package com.vk.dwzkf.tglib.botcore.context.photos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.telegram.telegrambots.meta.api.objects.PhotoSize;

import java.awt.image.BufferedImage;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Function;

/**
 * @author Roman Shageev
 * @since 30.11.2023
 */
@Getter
@Setter
@AllArgsConstructor
public abstract class MessagePhoto {
    @AllArgsConstructor
    public enum EPhotoSize {
        SMALL(size -> 0),
        MEDIUM(size -> size / 2),
        LARGE(size -> size - 1);
        private final Function<Integer, Integer> indexProvider;

        private int index(int size) {
            return indexProvider.apply(size);
        }
    }

    protected List<PhotoSize> variants;
    private final ReentrantLock rwLock = new ReentrantLock();

    private final Map<Integer, BufferedImage> downloaded = new ConcurrentHashMap<>();


    public BufferedImage download(EPhotoSize size) throws DownloadFailedException {
        int idx = size.index(variants.size());
        BufferedImage img = downloaded.get(idx);
        if (img != null) return img;
        try {
            rwLock.lock();
            if (downloaded.get(idx) != null) return downloaded.get(idx);
            BufferedImage bufferedImage = download0(variants.get(size.index(variants.size())));
            downloaded.put(idx, bufferedImage);
            return bufferedImage;
        } finally {
            rwLock.unlock();
        }
    }

    public BufferedImage download() throws DownloadFailedException {
        return download(EPhotoSize.MEDIUM);
    }

    protected abstract BufferedImage download0(PhotoSize photoSize) throws DownloadFailedException;
}
