import { useEffect, useRef } from "react"
import useAuthenticatedFetch from "@hooks/useAuthenticatedFetch";

export type UserProfilePicManager = {
    get: (login: string) => Promise<string>,
    delete: (login: string) => void
}

export default function useUserProfilePic(): UserProfilePicManager {
    const profilePicsRef = useRef<{ [login in string]: string }>({});
    const authenticatedFetch = useAuthenticatedFetch();

    useEffect(() => () => {
        Object.values(profilePicsRef.current)
            .forEach(URL.revokeObjectURL)
    }, []);

    return {
        get: async (login: string) => {
            if (profilePicsRef.current[login] !== undefined)
                return profilePicsRef.current[login];

            const response = await authenticatedFetch(`http://localhost:8080/user/${login}/profilePic`)

            if (!response.ok)
                return "default_profilePic";

            const blob = await response.blob();

            const url = URL.createObjectURL(blob);
            
            profilePicsRef.current[login] = url;

            return url;
        },
        delete: (login: string) => {
            if (profilePicsRef.current[login] !== undefined)
                URL.revokeObjectURL(profilePicsRef.current[login]);

            delete profilePicsRef.current[login];
        }
    }
}