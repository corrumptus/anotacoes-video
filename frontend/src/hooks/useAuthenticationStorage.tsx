"use client";

const TOKEN_JWT_KEY = "tokenJWT";

type AuthenticationStorage = {
    get: () => string | null,
    set: (token: string) => void,
    unset: () => void
};

export default function useAuthenticationStorage(): AuthenticationStorage {
    return {
        get: () => localStorage.getItem(TOKEN_JWT_KEY),
        set: (token: string) => localStorage.setItem(TOKEN_JWT_KEY, token),
        unset: () => localStorage.removeItem(TOKEN_JWT_KEY)
    };
}