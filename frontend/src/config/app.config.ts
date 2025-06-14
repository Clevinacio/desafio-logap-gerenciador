export const APP_CONFIG = {
  app: {
    name: 'Gerenciador de Vendas',
    version: '1.0.0',
  },
  ui: {
    theme: {
      defaultTheme: 'system' as const,
      storageKey: 'vite-ui-theme',
    },
    toast: {
      position: 'top-center' as const,
      reverseOrder: false,
    },
  },
  auth: {
    tokenKey: 'authToken',
  },
} as const;
