"use client";

import { useRouter } from "next/navigation";
import useAuthenticationStorage from "@hooks/useAuthenticationStorage";

export enum AuthenticationType {
    SIGNUP = "signup",
    LOGIN = "login"
}

export type AuthenticationInfos<T extends AuthenticationType> =
    T extends AuthenticationType.LOGIN ?
        {
            login: string,
            password: string
        }
        :
        {
            login: string,
            password: string,
            profilePic: File | undefined
        }

export default function useAuthenticate(type: AuthenticationType) {
    const router = useRouter();
    const authenticationStorage = useAuthenticationStorage();

    return async (infos: AuthenticationInfos<typeof type>) => {
        try {
            const response = type === AuthenticationType.LOGIN ?
                await fetch("http://localhost:8080/user/login", {
                    method: "POST",
                    headers: {
                        "content-type": "application/json"
                    },
                    body: JSON.stringify(infos)
                })
                :
                await fetch("http://localhost:8080/user/signup", {
                    method: "POST",
                    body: Object.entries(infos).reduce((acc, cur) => {
                        acc.append(cur[0], (cur[1] as string | Blob));
                        return acc;
                    }, new FormData())
                });

            const result = await response.json();
    
            if (!response.ok) {
                alert((result as { error: string }).error);
                return;
            }

            authenticationStorage.set((result as { token: string }).token);

            router.push("/user");
        } catch {
            alert("Não foi possível se conectar ao servidor, tente novamente");
        }
    }
}