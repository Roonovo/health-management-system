import request from './config'

/**
 * 上传病历文件
 */
export function uploadMedicalFile(formData) {
  return request({
    url: '/file/upload',
    method: 'post',
    data: formData,
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  })
}

/**
 * 获取文件列表
 */
export function getFileList(params) {
  return request({
    url: '/file/list',
    method: 'get',
    params
  })
}

/**
 * 获取文件详情
 */
export function getFileDetail(id) {
  return request({
    url: `/file/${id}`,
    method: 'get'
  })
}

/**
 * 下载文件
 */
export function downloadFile(id) {
  return request({
    url: `/file/${id}/download`,
    method: 'get',
    responseType: 'blob'
  })
}

/**
 * 删除文件
 */
export function deleteFile(id) {
  return request({
    url: `/file/${id}`,
    method: 'delete'
  })
}

/**
 * OCR识别
 */
export function ocrRecognize(formData) {
  return request({
    url: '/file/ocr',
    method: 'post',
    data: formData,
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  })
}