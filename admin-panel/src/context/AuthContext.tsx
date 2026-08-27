import { createContext, useContext, useState, useCallback, type ReactNode } from "react";
import { API_BASE_URL } from "../config";

interface AuthContextType {
    accessToken: string | null;
    login: (email: string, password: string) => Promise<void>;
    logout: () => Promise<void>;
}

const AuthContext = createContext<AuthContextType | undefined>(undefined);

export function AuthProvider({ children }: { children: ReactNode }) {
    const [accessToken, setAccessToken] = useState<string | null>(null);

    const login = useCallback(async (email: string, password: string) => {
        const response = await fetch(`${API_BASE_URL}/auth/login`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(
                { email,
                    password
                })
        });
        if (!response.ok) {
            // Handle error response message more appropriately, e.g. output the server error message, if this exists?
            throw new Error('Invalid username or password');
        }

        const data = await response.json();
        setAccessToken(data.accessToken);
    }, []);

    const logout = useCallback(async () => {
        await fetch(`${API_BASE_URL}/auth/logout`, {
            method: 'POST',
            credentials: 'include'
        });
    }), []);
}

