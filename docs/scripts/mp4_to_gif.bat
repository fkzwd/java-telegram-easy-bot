ffmpeg -i %1 ^
    -y ^
    -vf "scale=320:-1:flags=lanczos,split[s0][s1];[s0]palettegen[p];[s1][p]paletteuse" ^
    -loop 0 %2