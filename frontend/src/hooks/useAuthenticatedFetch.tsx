import useAuthenticationStorage from "@hooks/useAuthenticationStorage";

export default function useAuthenticatedFetch() {
    const authenticationStorage = useAuthenticationStorage();

    return (...args: Parameters<typeof fetch>) => {
        const token = authenticationStorage.get();

        if (token === null)
            throw new Error("Token is missing");

        if (args[1] === undefined)
            return fetch(args[0], {
                headers: {
                    "Authorization": token
                }
            });

        return fetch(args[0], {
            ...args[1],
            headers: {
                ...args[1].headers,
                "Authorization": token
            }
        });
    };
}