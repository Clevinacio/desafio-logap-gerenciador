/* eslint-disable react-refresh/only-export-components */
import { createContext, useState, type ReactNode, useEffect } from "react";
import { jwtDecode } from "jwt-decode";
import { APP_CONFIG } from "../config/app.config";

interface User {
  sub: string;
  name: string;
  roles: string[];
}

interface AuthContextType {
  isAuthenticated: boolean;
  isLoading: boolean;
  user: User | null;
  login: (token: string) => void;
  logout: () => void;
}

export const AuthContext = createContext<AuthContextType | undefined>(
  undefined
);

export const AuthProvider = ({ children }: { children: ReactNode }) => {
  const [user, setUser] = useState<User | null>(null);
  const [isLoading, setIsLoading] = useState<boolean>(true);

  useEffect(() => {
    const token = localStorage.getItem(APP_CONFIG.auth.tokenKey);
    if (token) {
      try {
        const decodedUser: User = jwtDecode(token);
        setUser(decodedUser);
      } catch (error) {
        console.error("Token inválido ou expirado", error);
        localStorage.removeItem(APP_CONFIG.auth.tokenKey);
      }
    }
    setIsLoading(false);
  }, []);

  const login = (token: string) => {
    localStorage.setItem(APP_CONFIG.auth.tokenKey, token);
    const decodedUser: User = jwtDecode(token);
    setUser(decodedUser);
    setIsLoading(false);
  };

  const logout = () => {
    localStorage.removeItem(APP_CONFIG.auth.tokenKey);
    setUser(null);
  };

  const value = {
    isAuthenticated: !!user,
    isLoading,
    user,
    login,
    logout,
  };

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
};
