export const ROUTES = {
  // Rotas públicas
  login: '/login',
  root: '/',
  
  // Rotas protegidas
  dashboard: '/dashboard',
  
  // Produtos
  produtos: '/produtos',
  
  // Cliente
  meusPedidos: '/meus-pedidos',
  carrinho: '/carrinho',
  pedidoConfirmado: (id: string) => `/pedido-confirmado/${id}`,
  
  // Vendedor/Admin
  pedidos: '/pedidos',
  pedidoDetalhado: (id: string) => `/pedidos/${id}`,
  
  // Admin
  usuarios: '/usuarios',
  relatorios: '/relatorios',
} as const;

export const USER_ROLES = {
  CLIENTE: 'CLIENTE',
  VENDEDOR: 'VENDEDOR',
  ADMINISTRADOR: 'ADMINISTRADOR',
} as const;

export type UserRole = typeof USER_ROLES[keyof typeof USER_ROLES];
