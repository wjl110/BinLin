import axios from 'axios'
import qs from 'qs'
import {ElMessage} from "element-plus";
import {sharedData, sharedMethod} from "./sharedData";

export const baseURL = '/api';
axios.defaults.baseURL = baseURL;
axios.defaults.timeout = 1000;
axios.defaults.headers.post['Content-Type'] = 'application/x-www-form-urlencoded';
axios.interceptors.request.use(function (config) {
  if(sharedData.token) {
    config.headers['token'] = sharedData.token
  }
  return config;
}, function (error) {
  return Promise.reject(error);
});

axios.interceptors.response.use(function (response) {
  if (response.data) {
    if (response.data instanceof Blob) {
      return response;
    }
    const code = Math.floor(response.data.code / 100);
    if (code === 2) {
      return response.data;
    }
    if (response.data.code === 401) {
      return response.data;
    }
    let type = code === 5 ? 'error' : 'warning'
    ElMessage({
      type: type,
      message: response.data.msg
    })
    return Promise.reject(new Error(response.data.msg));
  }
  return response.data;
}, function (error) {
  return Promise.reject(error);
});

export const get = (url, params) => {
  return axios({
    url,
    params,
    method: 'GET'
  })
}
export const postJson = (url, data) => {
  return axios({
    url: url,
    method: 'post',
    data: data
  })
}
export const post = (url, data, delEmpty) => {
  if (data instanceof FormData) {
    if (delEmpty) deleteEmptyFormData(data)
    return axios({
      url,
      data: data,
      method: 'POST',
      headers: {
        'Content-Type': 'multipart/form-data'
      }
    })
  } else {
    if (delEmpty) deleteEmpty(data)
    return axios({
      url,
      data: qs.stringify(data),
      method: 'POST',
      headers: {
        'Content-Type': 'application/x-www-form-urlencoded'
      }
    })
  }
}
export const put = (url, data, delEmpty) => {
  if (delEmpty) deleteEmpty(data)
  return axios({
    url,
    data: qs.stringify(data),
    method: 'PUT',
    headers: {
      'Content-Type': 'application/x-www-form-urlencoded'
    }
  })
}
export const del = (url, params) => {
  return axios({
    url,
    params: params,
    method: 'DELETE'
  })
}
export const down = (url, param) => {
  axios({
    url: url,
    method: 'GET',
    params: param,
    responseType: 'blob'
  }).then(res => {
    const blob = new Blob([res.data], { type: res.data.type })
    const downloadElement = document.createElement('a')
    const href = window.URL.createObjectURL(blob)
    downloadElement.href = href
    downloadElement.download = decodeURI(res.headers['file-name'])
    document.body.appendChild(downloadElement)
    downloadElement.click()
    document.body.removeChild(downloadElement)
    window.URL.revokeObjectURL(href)
  })
}
const deleteEmpty = (obj) => {
  for (const k in obj) {
    if (obj[k] === null || obj[k] === undefined) {
      delete obj[k]
    }
  }
}
const deleteEmptyFormData = (formData) => {
  formData.forEach((v, k) => {
    if (v === null || v === undefined) {
      formData.delete(k)
    }
  })
}
