import { onBeforeUnmount, ref } from 'vue'

export interface UseMediaUploadOptions {
  maxPhotos?: number
  maxFileSizeMb?: number
}

const DEFAULT_MAX_PHOTOS = 20
const DEFAULT_MAX_FILE_SIZE_MB = 10
const MAX_IMAGE_DIMENSION = 1920

export const useMediaUpload = (options: UseMediaUploadOptions = {}) => {
  const MAX_PHOTOS = options.maxPhotos || DEFAULT_MAX_PHOTOS
  const MAX_FILE_SIZE_MB = options.maxFileSizeMb || DEFAULT_MAX_FILE_SIZE_MB
  const MAX_FILE_SIZE_BYTES = MAX_FILE_SIZE_MB * 1024 * 1024

  // Photos
  const photos = ref<File[]>([])
  const photoPreviews = ref<string[]>([])
  const isOptimizingPhotos = ref(false)

  // Video
  const videoFile = ref<File | null>(null)

  // --- Photo Logic ---
  const handlePhotoUpload = async (event: Event) => {
    const input = event.target as HTMLInputElement
    if (!input.files?.length) {
      return
    }

    const availableSlots = MAX_PHOTOS - photos.value.length
    if (availableSlots <= 0) {
      alert(`사진은 최대 ${MAX_PHOTOS}장까지 첨부할 수 있습니다.`)
      input.value = ''
      return
    }

    const incomingFiles = Array.from(input.files).slice(0, availableSlots)
    try {
      isOptimizingPhotos.value = true
      for (const file of incomingFiles) {
        const optimizedFile = await optimizeImage(file, MAX_FILE_SIZE_BYTES)
        addPhoto(optimizedFile)
      }
    } catch (error) {
      console.error('사진 최적화 중 오류', error)
      alert('이미지 준비 중 문제가 발생했습니다. 다른 파일을 다시 시도해주세요.')
    } finally {
      isOptimizingPhotos.value = false
      input.value = ''
    }
  }

  const addPhoto = (file: File) => {
    photos.value.push(file)
    photoPreviews.value.push(URL.createObjectURL(file))
  }

  const removePhoto = (index: number) => {
    const preview = photoPreviews.value[index]
    if (preview) {
      URL.revokeObjectURL(preview)
    }
    photos.value.splice(index, 1)
    photoPreviews.value.splice(index, 1)
  }

  const clearPhotos = () => {
    photoPreviews.value.forEach((src) => URL.revokeObjectURL(src))
    photos.value = []
    photoPreviews.value = []
  }

  // --- Video Logic ---
  const handleVideoUpload = (file: File) => {
    videoFile.value = file
  }

  const removeVideo = () => {
    videoFile.value = null
  }

  onBeforeUnmount(() => {
    clearPhotos()
  })

  return {
    photos,
    photoPreviews,
    isOptimizingPhotos,
    handlePhotoUpload,
    removePhoto,
    clearPhotos,
    videoFile,
    handleVideoUpload,
    removeVideo,
    MAX_PHOTOS,
    MAX_FILE_SIZE_MB,
  }
}

// --- Optimization Helpers ---
const optimizeImage = async (file: File, maxSizeBytes: number): Promise<File> => {
  if (typeof window === 'undefined') {
    return file
  }
  try {
    const dataUrl = await readFileAsDataUrl(file)
    const image = await loadImageElement(dataUrl)
    const needsResize = Math.max(image.width, image.height) > MAX_IMAGE_DIMENSION
    const exceedsSize = file.size > maxSizeBytes
    if (!needsResize && !exceedsSize) {
      return file
    }

    const { width, height } = getScaledDimensions(image.width, image.height, MAX_IMAGE_DIMENSION)
    const canvas = document.createElement('canvas')
    canvas.width = width
    canvas.height = height
    const ctx = canvas.getContext('2d')
    if (!ctx) {
      return file
    }
    ctx.drawImage(image, 0, 0, width, height)

    const outputType = file.type === 'image/png' ? 'image/png' : 'image/jpeg'
    const quality = outputType === 'image/png' ? undefined : 0.82
    const blob = await new Promise<Blob | null>((resolve) =>
      canvas.toBlob(resolve, outputType, quality),
    )
    if (!blob) {
      return file
    }

    const optimizedName = buildOptimizedFileName(file.name, outputType)
    return new File([blob], optimizedName, { type: outputType, lastModified: Date.now() })
  } catch (error) {
    console.warn('optimizeImage failed, fallback to original file', error)
    return file
  }
}

const readFileAsDataUrl = (file: File) => {
  return new Promise<string>((resolve, reject) => {
    const reader = new FileReader()
    reader.onload = () => resolve(reader.result as string)
    reader.onerror = reject
    reader.readAsDataURL(file)
  })
}

const loadImageElement = (src: string) => {
  return new Promise<HTMLImageElement>((resolve, reject) => {
    const img = new Image()
    img.decoding = 'async'
    img.onload = () => resolve(img)
    img.onerror = reject
    img.src = src
  })
}

const getScaledDimensions = (width: number, height: number, maxSize: number) => {
  if (width <= maxSize && height <= maxSize) {
    return { width, height }
  }
  const ratio = width > height ? maxSize / width : maxSize / height
  return {
    width: Math.round(width * ratio),
    height: Math.round(height * ratio),
  }
}

const buildOptimizedFileName = (originalName: string, mimeType: string) => {
  const extension = mimeType === 'image/png' ? 'png' : 'jpg'
  const base = originalName.replace(/\.[^.]+$/, '')
  return `${base}_optimized.${extension}`
}
