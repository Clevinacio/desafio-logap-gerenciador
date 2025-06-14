import axios from 'axios';
import { API_CONFIG } from '../config/api.config';
import { APP_CONFIG } from '../config/app.config';

const api = axios.create({
    baseURL: API_CONFIG.baseURL,
    timeout: API_CONFIG.timeout,
    headers: API_CONFIG.headers,
});

// Interceptor para adicionar token automaticamente
api.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem(APP_CONFIG.auth.tokenKey);
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

// Interceptor para lidar com respostas de erro
api.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response?.status === 401) {
      localStorage.removeItem(APP_CONFIG.auth.tokenKey);
      window.location.href = '/login';
    }
    return Promise.reject(error);
  }
);

export default api;