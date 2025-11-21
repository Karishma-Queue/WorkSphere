import axios from 'axios';

const baseURL = import.meta.env.VITE_API_BASE_URL ?? 'http://localhost:8080';

export const apiClient = axios.create({
  baseURL,
  withCredentials: true
});

apiClient.interceptors.request.use((config) => {
  const raw = localStorage.getItem('worksphere.session');
  if (raw) {
    try {
      const { token } = JSON.parse(raw);
      if (token) {
        config.headers = {
          ...config.headers,
          Authorization: `Bearer ${token}`
        };
      }
    } catch {
      /* swallow */
    }
  }
  return config;
});

apiClient.interceptors.response.use(
  (response) => response,
  (error) => {
    if (import.meta.env.DEV) {
      console.error('[API ERROR]', error?.response ?? error);
    }
    return Promise.reject(error);
  }
);

