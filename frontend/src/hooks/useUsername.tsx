import { useEffect, useState } from "react";
import useAuthenticatedFetch from "@hooks/useAuthenticatedFetch";

export default function useUsername(): string | undefined {
    const authenticatedFetch = useAuthenticatedFetch();
    const [ username, setUsername ] = useState<string>();

    useEffect(() => {
        (async () => {
            const response = await authenticatedFetch("http://localhost:8080/user/name");

            if (!response.ok)
                return;

            const result = await response.json();

            setUsername(result.name);
        })();
    }, []);

    return username;
}