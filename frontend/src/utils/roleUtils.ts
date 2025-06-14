import type { UserRole } from '../config/constants';
import { USER_ROLES } from '../config/constants';

export const roleUtils = {
  /**
   * Remove o prefixo "ROLE_" da role se existir
   */
  normalizeRole: (role: string): string => {
    return role.replace('ROLE_', '');
  },

  /**
   * Verifica se o usuário possui uma role específica
   */
  hasRole: (userRoles: string[] | undefined, targetRole: UserRole): boolean => {
    if (!userRoles) return false;
    return userRoles.some(role => roleUtils.normalizeRole(role) === targetRole);
  },

  /**
   * Verifica se o usuário possui pelo menos uma das roles especificadas
   */
  hasAnyRole: (userRoles: string[] | undefined, targetRoles: UserRole[]): boolean => {
    if (!userRoles) return false;
    return targetRoles.some(role => roleUtils.hasRole(userRoles, role));
  },

  /**
   * Verifica se o usuário é cliente
   */
  isCliente: (userRoles: string[] | undefined): boolean => {
    return roleUtils.hasRole(userRoles, USER_ROLES.CLIENTE);
  },

  /**
   * Verifica se o usuário é vendedor
   */
  isVendedor: (userRoles: string[] | undefined): boolean => {
    return roleUtils.hasRole(userRoles, USER_ROLES.VENDEDOR);
  },

  /**
   * Verifica se o usuário é administrador
   */
  isAdministrador: (userRoles: string[] | undefined): boolean => {
    return roleUtils.hasRole(userRoles, USER_ROLES.ADMINISTRADOR);
  },

  /**
   * Verifica se o usuário pode gerenciar pedidos (vendedor ou admin)
   */
  canManageOrders: (userRoles: string[] | undefined): boolean => {
    return roleUtils.hasAnyRole(userRoles, [USER_ROLES.VENDEDOR, USER_ROLES.ADMINISTRADOR]);
  },

  /**
   * Verifica se o usuário pode acessar área administrativa
   */
  canAccessAdmin: (userRoles: string[] | undefined): boolean => {
    return roleUtils.hasRole(userRoles, USER_ROLES.ADMINISTRADOR);
  },
};
