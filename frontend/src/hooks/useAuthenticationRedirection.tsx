"use client";

import { useRouter } from "next/navigation";
import { useEffect } from "react";
import useAuthenticationStorage from "@hooks/useAuthenticationStorage";

export enum WhenRedirect {
    AUTHENTICATED,
    NON_AUTHENTICATED
}

export default function useAuthenticationRedirection(when: WhenRedirect) {
    const router = useRouter();
    const authenticationStorage = useAuthenticationStorage();

    useEffect(() => {
        switch (when) {
            case WhenRedirect.AUTHENTICATED:
                if (authenticationStorage.get() !== null)
                    router.replace("/user");
                break;
            case WhenRedirect.NON_AUTHENTICATED:
                if (authenticationStorage.get() === null)
                    router.replace("/signup");
                break;
        }
    }, []);
}