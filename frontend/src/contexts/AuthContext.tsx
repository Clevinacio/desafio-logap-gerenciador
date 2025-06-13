/* eslint-disable react-refresh/only-export-components */
import { createContext, useState, type ReactNode, useEffect } from "react";
import { jwtDecode } from "jwt-decode";
import api from "../services/api";

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
    const token = localStorage.getItem("authToken");
    if (token) {
      try {
        const decodedUser: User = jwtDecode(token);
        setUser(decodedUser);
        api.defaults.headers.common["Authorization"] = `Bearer ${token}`;
      } catch (error) {
        console.error("Token inválido ou expirado", error);
        localStorage.removeItem("authToken");
      } finally {
        setIsLoading(false);
      }
    }
  }, []);

  const login = (token: string) => {
    localStorage.setItem("authToken", token);
    const decodedUser: User = jwtDecode(token);
    setUser(decodedUser);
    api.defaults.headers.common["Authorization"] = `Bearer ${token}`;
  };

  const logout = () => {
    localStorage.removeItem("authToken");
    setUser(null);
    delete api.defaults.headers.common["Authorization"];
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
