/**
 * 클라이언트 측 이미지 최적화 (압축 및 리사이징) Composable
 */
export const useImageOptimization = () => {
  /**
   * 이미지를 압축합니다.
   * @param file 원본 파일
   * @param maxWidth 최대 너비 (기본 1920px)
   * @param maxSizeMB 목표 최대 크기 (기본 1MB)
   * @param quality 압축 퀄리티 (0~1)
   */
  const compressImage = async (
    file: File,
    maxWidth: number = 1920,
    maxSizeMB: number = 1,
    quality: number = 0.8,
  ): Promise<File> => {
    // 유효성 검사: 이미지가 아니면 그대로 반환
    if (!file.type.match(/image.*/)) {
      return file;
    }

    return new Promise((resolve, reject) => {
      const reader = new FileReader();
      reader.readAsDataURL(file);
      reader.onload = (event) => {
        const img = new Image();
        img.src = event.target?.result as string;
        img.onload = () => {
          // 크기 계산 (비율 유지)
          let width = img.width;
          let height = img.height;

          if (width > maxWidth) {
            height = Math.round((height * maxWidth) / width);
            width = maxWidth;
          }

          // Canvas 그리기를 통한 압축
          const canvas = document.createElement("canvas");
          canvas.width = width;
          canvas.height = height;
          const ctx = canvas.getContext("2d");
          ctx?.drawImage(img, 0, 0, width, height);

          // Blob으로 변환
          canvas.toBlob(
            (blob) => {
              if (!blob) {
                resolve(file); // 실패 시 원본 반환
                return;
              }

              // 용량이 목표보다 크면 원본보다 작아졌는지 확인 후 반환
              // (여기서는 단순하게 quality로만 압축하지만, 반복 루프로 맞출 수도 있음.
              //  일단 단순 리사이징+quality 적용이 성능상 유리)

              const compressedFile = new File([blob], file.name, {
                type: "image/jpeg", // JPEG로 변환하여 압축 효율 증대
                lastModified: Date.now(),
              });

              // 만약 압축했는데도 원본보다 크다면(매우 드뭄) 원본 사용
              if (compressedFile.size > file.size) {
                resolve(file);
              } else {
                resolve(compressedFile);
              }
            },
            "image/jpeg",
            quality,
          );
        };
        img.onerror = (err) => reject(err);
      };
      reader.onerror = (err) => reject(err);
    });
  };

  return {
    compressImage,
  };
};
