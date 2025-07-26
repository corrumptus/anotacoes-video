import { useRouter } from "next/navigation";
import { AppRouterInstance } from "next/dist/shared/lib/app-router-context.shared-runtime";
import { Dispatch, SetStateAction, useEffect, useState } from "react";
import useAuthenticatedFetch from "@hooks/useAuthenticatedFetch";
import useUserProfilePic, { UserProfilePicManager } from "@hooks/useUserProfilePic";
import { Video, VideoVisibility, VisibilityType } from "@hooks/useVideoStore";

export type AsyncAction = "video-loading" |
    "video-edit" |
    "video-user-privileges" |
    "video-user-add" |
    "video-user-remove" |
    "video-delete" |
    "anotation-loading" |
    "anotation-add" |
    "anotation-edit" |
    "anotation-delete";

export type VideoManager = {
    video: Video | undefined;
    edit: (videoUpdate: VideoUpdate) => void;
    changeVisibility: (newVisibility: VisibilityType) => void;
    changeCanAnotate: (newCanAnotate: boolean, visitant: string | undefined) => void;
    addUser: (userLogin: string) => void;
    removeUser: (userLogin: string) => void;
    deleteVideo: () => void;
    findUsers: (subUserLogin: string) => Promise<FindedUser[]>;
}

export type AnotationsManager = {
    anotations: Anotation[];
    addAnotation: (newAnotation: AnotationPublicFields) => void;
    edit: (anotation: Anotation, anotationUpdate: AnotationPublicFields) => void;
    deleteAnotation: (anotation: Anotation) => void;
}

export type VideoUpdate = {
    title: string,
    description: string
}

export type FindedUser = {
    id: string,
    name: string,
    profilePic?: string
}

export type Anotation = {
    id: string,
    anotation: string,
    videoInstant: number,
    videoId: string,
    userId: string,
    userName: string,
    userPic?: string
}

export type AnotationPublicFields = {
    anotation: string,
    videoInstant: number
}

async function getVideo(
    videoId: string,
    authenticatedFetch: ReturnType<typeof useAuthenticatedFetch>,
    setVideo: Dispatch<SetStateAction<Video | undefined>>,
    setAsyncAction: Dispatch<SetStateAction<AsyncAction | undefined>>,
    profilePicManager: UserProfilePicManager
) {
    try {
        const response = await authenticatedFetch(`http://localhost:8080/video/${videoId}/infos`);

        const result = await response.json();

        if (!response.ok) {
            alert((result as { error: string }).error);
            setAsyncAction(undefined);
            return;
        }

        const videoResponse = await authenticatedFetch(`http://localhost:8080/video/${videoId}/file`);

        if (!videoResponse.ok) {
            alert("Não foi possível carregar o vídeo");
            setAsyncAction(undefined);
            return;
        }

        const videoResult = await videoResponse.blob();

        if ((result as Video).visibility.canAnotateVisitants !== null)
            await Promise.all((result as Video).visibility.canAnotateVisitants
                ?.map(async ({ name }, i, array) => {
                    const url = await profilePicManager.get(name);

                    array[i].profilePic = url;
                }) as Promise<void>[]
            );

        if ((result as Video).visibility.canNotAnotateVisitants !== null)
            await Promise.all((result as Video).visibility.canNotAnotateVisitants
                ?.map(async ({ name }, i, array) => {
                    const url = await profilePicManager.get(name);

                    array[i].profilePic = url;
                }) as Promise<void>[]
            );

        const videoUrl = URL.createObjectURL(videoResult);

        setVideo({ ...result, video: videoUrl });
        setAsyncAction("anotation-loading");
    } catch {
        alert("Não foi possível acessar o vídeo");
        setAsyncAction(undefined);
    }
}

async function changeVideoInfos(
    videoId: string,
    infos: VideoUpdate,
    authenticatedFetch: ReturnType<typeof useAuthenticatedFetch>,
    setVideo: Dispatch<SetStateAction<Video | undefined>>,
    setAsyncAction: Dispatch<SetStateAction<AsyncAction | undefined>>
) {
    try {
        setAsyncAction("video-edit");

        const response = await authenticatedFetch("http://localhost:8080/video/" + videoId, {
            method: "PUT",
            headers: {
                "content-type": "application/json"
            },
            body: JSON.stringify(infos)
        });

        const result = await response.json();

        if (!response.ok) {
            alert((result as { error: string }).error);
            setAsyncAction(undefined);
            return;
        }

        setVideo(cur => ({ ...cur!, ...response }));
        setAsyncAction(undefined);
    } catch {
        alert("Nãos foi possível se conectar ao servidor");
        setAsyncAction(undefined);
    }
}

async function changeVideoVisibility(
    video: Video,
    newVisibility: VisibilityType,
    authenticatedFetch: ReturnType<typeof useAuthenticatedFetch>,
    setVideo: Dispatch<SetStateAction<Video | undefined>>,
    setAsyncAction: Dispatch<SetStateAction<AsyncAction | undefined>>,
    profilePicManager: UserProfilePicManager
) {
    try {
        if (video.visibility.type === newVisibility)
            return;

        setAsyncAction("video-user-privileges");

        const response = await authenticatedFetch(`http://localhost:8080/video/${video.id}/visibility`, {
            method: "PUT",
            headers: {
                "content-type": "application/json"
            },
            body: JSON.stringify({ visibility: newVisibility })
        });

        const result = await response.json();

        if (!response.ok) {
            alert((result as { error: string }).error);
            setAsyncAction(undefined);
            return;
        }

        if ((result as VideoVisibility).canAnotateVisitants !== null)
            await Promise.all((result as VideoVisibility).canAnotateVisitants
                ?.map(async ({ name }, i, array) => {
                    const url = await profilePicManager.get(name);

                    array[i].profilePic = url;
                }) as Promise<void>[]
            );

        if ((result as VideoVisibility).canNotAnotateVisitants !== null)
            await Promise.all((result as VideoVisibility).canNotAnotateVisitants
                ?.map(async ({ name }, i, array) => {
                    const url = await profilePicManager.get(name);

                    array[i].profilePic = url;
                }) as Promise<void>[]
            );

        setVideo(cur => ({
            ...cur!,
            visibility: result
        }));
        setAsyncAction(undefined);
    } catch {
        alert("Não foi possível se conectar ao servidor");
        setAsyncAction(undefined);
    }
}

async function changeVideoCanAnotate(
    video: Video,
    newCanAnotate: boolean,
    visitant: string | undefined,
    authenticatedFetch: ReturnType<typeof useAuthenticatedFetch>,
    setVideo: Dispatch<SetStateAction<Video | undefined>>,
    setAsyncAction: Dispatch<SetStateAction<AsyncAction | undefined>>,
    profilePicManager: UserProfilePicManager
) {
    try {
        if (video.visibility.type === VisibilityType.PRIVATE)
            return;

        if (video.visibility.type === VisibilityType.RESTRICTED && visitant === undefined)
            return;

        if (video.visibility.type === VisibilityType.PUBLIC && visitant !== undefined)
            return;

        setAsyncAction("video-user-privileges");

        const response = await authenticatedFetch(`http://localhost:8080/video/${video.id}/visibility/canAnotate`, {
            method: "PUT",
            headers: {
                "content-type": "application/json"
            },
            body: JSON.stringify({ canAnotate: newCanAnotate, visitant: visitant })
        });

        const result = await response.json();

        if (!response.ok) {
            alert((result as { error: string }).error);
            setAsyncAction(undefined);
            return;
        }

        if ((result as VideoVisibility).canAnotateVisitants !== null)
            await Promise.all((result as VideoVisibility).canAnotateVisitants
                ?.map(async ({ name }, i, array) => {
                    const url = await profilePicManager.get(name);

                    array[i].profilePic = url;
                }) as Promise<void>[]
            );

        if ((result as VideoVisibility).canNotAnotateVisitants !== null)
            await Promise.all((result as VideoVisibility).canNotAnotateVisitants
                ?.map(async ({ name }, i, array) => {
                    const url = await profilePicManager.get(name);

                    array[i].profilePic = url;
                }) as Promise<void>[]
            );

        setVideo(cur => ({
            ...cur!,
            visibility: result
        }));
        setAsyncAction(undefined);
    } catch {
        alert("Não foi possível se conectar ao servidor");
        setAsyncAction(undefined);
    }
}

async function addUser(
    videoId: string,
    user: string,
    authenticatedFetch: ReturnType<typeof useAuthenticatedFetch>,
    setVideo: Dispatch<SetStateAction<Video | undefined>>,
    setAsyncAction: Dispatch<SetStateAction<AsyncAction | undefined>>,
    profilePicManager: UserProfilePicManager
) {
    try {
        setAsyncAction("video-user-add");

        const response = await authenticatedFetch(`http://localhost:8080/video/${videoId}/visibility/users`, {
            method: "POST",
            headers: {
                "content-type": "application/json"
            },
            body: JSON.stringify({ userLogin: user })
        });

        const result = await response.json();

        if (!response.ok) {
            alert((result as { error: string }).error);
            setAsyncAction(undefined);
            return;
        }

        if ((result as VideoVisibility).canAnotateVisitants !== null)
            await Promise.all((result as VideoVisibility).canAnotateVisitants
                ?.map(async ({ name }, i, array) => {
                    const url = await profilePicManager.get(name);

                    array[i].profilePic = url;
                }) as Promise<void>[]
            );

        if ((result as VideoVisibility).canNotAnotateVisitants !== null)
            await Promise.all((result as VideoVisibility).canNotAnotateVisitants
                ?.map(async ({ name }, i, array) => {
                    const url = await profilePicManager.get(name);

                    array[i].profilePic = url;
                }) as Promise<void>[]
            );

        setVideo(cur => ({
            ...cur!,
            visibility: result
        }));
        setAsyncAction(undefined);
    } catch {
        alert("Não foi possível se conectar ao servidor");
        setAsyncAction(undefined);
    }
}

async function removeUser(
    videoId: string,
    user: string,
    authenticatedFetch: ReturnType<typeof useAuthenticatedFetch>,
    setVideo: Dispatch<SetStateAction<Video | undefined>>,
    setAsyncAction: Dispatch<SetStateAction<AsyncAction | undefined>>,
    profilePicManager: UserProfilePicManager
) {
    try {
        setAsyncAction("video-user-remove");

        const response = await authenticatedFetch(`http://localhost:8080/video/${videoId}/visibility/users/${user}`, {
            method: "DELETE"
        });

        const result = await response.json();

        if (!response.ok) {
            alert((result as { error: string }).error);
            setAsyncAction(undefined);
            return;
        }

        if ((result as VideoVisibility).canAnotateVisitants !== null)
            await Promise.all((result as VideoVisibility).canAnotateVisitants
                ?.map(async ({ name }, i, array) => {
                    const url = await profilePicManager.get(name);

                    array[i].profilePic = url;
                }) as Promise<void>[]
            );

        if ((result as VideoVisibility).canNotAnotateVisitants !== null)
            await Promise.all((result as VideoVisibility).canNotAnotateVisitants
                ?.map(async ({ name }, i, array) => {
                    const url = await profilePicManager.get(name);

                    array[i].profilePic = url;
                }) as Promise<void>[]
            );

        profilePicManager.delete(user);

        setVideo(cur => ({
            ...cur!,
            visibility: result
        }));
        setAsyncAction(undefined);
    } catch {
        alert("Não foi possível se conectar ao servidor");
        setAsyncAction(undefined);
    }
}

async function deleteVideo(
    video: Video,
    authenticatedFetch: ReturnType<typeof useAuthenticatedFetch>,
    router: AppRouterInstance,
    setAsyncAction: Dispatch<SetStateAction<AsyncAction | undefined>>
) {
    try {
        setAsyncAction("video-delete");

        const response = await authenticatedFetch("http://localhost:8080/video/" + video.id, {
            method: "DELETE"
        });

        if (!response.ok) {
            const result = await response.json();
            alert((result as { error: string }).error);
            setAsyncAction(undefined);
            return;
        }

        URL.revokeObjectURL(video.video!);

        router.replace("/user");
        setAsyncAction(undefined);
    } catch {
        alert("Não foi possível se conectar ao servidor");
        setAsyncAction(undefined);
    }
}

async function addAnotation(
    videoId: string,
    newAnotation: AnotationPublicFields,
    authenticatedFetch: ReturnType<typeof useAuthenticatedFetch>,
    setAnotations: Dispatch<SetStateAction<Anotation[]>>,
    setAsyncAction: Dispatch<SetStateAction<AsyncAction | undefined>>,
    profilePicManager: UserProfilePicManager
) {
    try {
        setAsyncAction("anotation-add");

        const response = await authenticatedFetch(`http://localhost:8080/video/${videoId}/anotation`, {
            method: "POST",
            headers: {
                "content-type": "application/json"
            },
            body: JSON.stringify(newAnotation)
        });

        const result = await response.json();

        if (!response.ok) {
            alert((result as { error: string }).error);
            setAsyncAction(undefined);
            return;
        }

        result.userPic = await profilePicManager.get(result.userName);

        setAnotations(cur => {
            const newAnotations: Anotation[] = [...cur!, result];

            newAnotations.sort((a, b) => a.videoInstant - b.videoInstant);

            return newAnotations;
        });
        setAsyncAction(undefined);
    } catch {
        alert("Não foi possível se conectar ao servidor");
        setAsyncAction(undefined);
    }
}

async function getAnotations(
    videoId: string,
    authenticatedFetch: ReturnType<typeof useAuthenticatedFetch>,
    setAnotations: Dispatch<SetStateAction<Anotation[]>>,
    setAsyncAction: Dispatch<SetStateAction<AsyncAction | undefined>>,
    profilePicManager: UserProfilePicManager
) {
    try {
        const response = await authenticatedFetch(`http://localhost:8080/video/${videoId}/anotation`);

        const results = await response.json();

        if (!response.ok) {
            alert((results as { error: string }).error);
            setAsyncAction(undefined);
            return;
        }

        const anotationWithUserPic = (results as Anotation[]).reduce<
            Record<Anotation["id"], Anotation>
        >(
            (acc, cur) => {
                acc[cur.id] = cur;
                return acc;
            },
            {}
        );

        await Promise.all(
            (results as Anotation[]).map(async a => {
                const url = await profilePicManager.get(a.userName);

                anotationWithUserPic[a.id].userPic = url;
            })
        );

        setAnotations(Object.values(anotationWithUserPic));
        setAsyncAction(undefined);
    } catch {
        alert("Não foi possível acessar as anotações");
        setAsyncAction(undefined);
    }
}

async function changeAnotation(
    anotation: Anotation,
    anotationUpdate: AnotationPublicFields,
    authenticatedFetch: ReturnType<typeof useAuthenticatedFetch>,
    setAnotations: Dispatch<SetStateAction<Anotation[]>>,
    setAsyncAction: Dispatch<SetStateAction<AsyncAction | undefined>>
) {
    try {
        setAsyncAction("anotation-edit");

        const response = await authenticatedFetch(`http://localhost:8080/video/${anotation.videoId}/anotation/${anotation.id}`, {
            method: "PUT",
            headers: {
                "content-type": "application/json"
            },
            body: JSON.stringify(anotationUpdate)
        });

        const result = await response.json();

        if (!response.ok) {
            alert((result as { error: string }).error);
            setAsyncAction(undefined);
            return;
        }

        setAnotations(cur => {
            const prevAnotation = cur.find(a => a.id === anotation.id);

            if (prevAnotation !== undefined) {
                prevAnotation.anotation = result.anotation;
                prevAnotation.videoInstant = result.videoInstant;
            }

            const newAnotations = [...cur];

            newAnotations.sort((a, b) => a.videoInstant - b.videoInstant);

            return newAnotations;
        });
        setAsyncAction(undefined);
    } catch {
        alert("Não foi possível se conectar ao servidor");
        setAsyncAction(undefined);
    }
}

async function deleteAnotation(
    anotation: Anotation,
    authenticatedFetch: ReturnType<typeof useAuthenticatedFetch>,
    setAnotations: Dispatch<SetStateAction<Anotation[]>>,
    setAsyncAction: Dispatch<SetStateAction<AsyncAction | undefined>>
) {
    try {
        setAsyncAction("anotation-delete");

        const response = await authenticatedFetch(`http://localhost:8080/video/${anotation.videoId}/anotation/${anotation.id}`, {
            method: "DELETE"
        });

        if (!response.ok) {
            const result = await response.json();
            alert((result as { error: string }).error);
            setAsyncAction(undefined);
            return;
        }

        setAnotations(cur => cur.filter(a => a.id !== anotation.id));
        setAsyncAction(undefined);
    } catch {
        alert("Não foi possível se conectar ao servidor");
        setAsyncAction(undefined);
    }
}

async function findUsers(
    subUserLogin: string,
    authenticatedFetch: ReturnType<typeof useAuthenticatedFetch>,
    profilePicManager: UserProfilePicManager
) {
    try {
        const response = await authenticatedFetch(`http://localhost:8080/user?sub=${subUserLogin}`);

        const result = await response.json();

        if (!response.ok) {
            alert((result as { error: string }).error);
            return [];
        }

        const optionsWithUserPic = (result as FindedUser[]).reduce<
            Record<FindedUser["id"], FindedUser>
        >(
            (acc, cur) => {
                acc[cur.id] = cur;
                return acc;
            },
            {}
        );

        await Promise.all(
            (result as FindedUser[]).map(async u => {
                const url = await profilePicManager.get(u.name);

                optionsWithUserPic[u.id].profilePic = url;
            })
        );

        return Object.values(optionsWithUserPic);
    } catch(error) {
        return [];
    }
}

export default function useVideoAndAnotations(id: string): { video: VideoManager, anotations: AnotationsManager, asyncAction: AsyncAction | undefined } {
    const router = useRouter();
    const authenticatedFetch = useAuthenticatedFetch();
    const userProfilePicManager = useUserProfilePic();
    const [ video, setVideo ] = useState<Video>();
    const [ anotations, setAnotations ] = useState<Anotation[]>([]);
    const [ asyncAction, setAsyncAction ] = useState<AsyncAction | undefined>("video-loading");

    useEffect(() => {
        getVideo(id, authenticatedFetch, setVideo, setAsyncAction, userProfilePicManager);
    }, []);

    useEffect(() => {
        if (asyncAction === "video-loading")
            return;

        if (video === undefined)
            router.push("/user");
    }, [asyncAction]);

    useEffect(() => {
        if (asyncAction !== "anotation-loading")
            return;

        getAnotations(id, authenticatedFetch, setAnotations, setAsyncAction, userProfilePicManager);
    }, [asyncAction]);

    useEffect(() => {
        if (video === undefined)
            return;

        return () => {
            URL.revokeObjectURL(video.video!);
        }
    }, [video === undefined]);

    return {
        video: {
            video: video,
            findUsers: async (subUserLogin: string) =>
                await findUsers(subUserLogin, authenticatedFetch, userProfilePicManager),
            edit: (videoUpdate: VideoUpdate) => asyncAction === undefined &&
                changeVideoInfos(video!.id, videoUpdate, authenticatedFetch, setVideo, setAsyncAction),
            changeVisibility: (newVisibility: VisibilityType) => asyncAction === undefined &&
                changeVideoVisibility(video as Video, newVisibility, authenticatedFetch, setVideo, setAsyncAction, userProfilePicManager),
            changeCanAnotate: (newCanAnotate: boolean, visitant: string | undefined) => asyncAction === undefined &&
                changeVideoCanAnotate(video as Video, newCanAnotate, visitant, authenticatedFetch, setVideo, setAsyncAction, userProfilePicManager),
            addUser: (userLogin: string) => asyncAction === undefined &&
                addUser(video!.id, userLogin, authenticatedFetch, setVideo, setAsyncAction, userProfilePicManager),
            removeUser: (userLogin: string) => asyncAction === undefined &&
                removeUser(video!.id, userLogin, authenticatedFetch, setVideo, setAsyncAction, userProfilePicManager),
            deleteVideo: () => asyncAction === undefined &&
                deleteVideo(video as Video, authenticatedFetch, router, setAsyncAction)
        },
        anotations: {
            anotations: anotations,
            addAnotation: (newAnotation: AnotationPublicFields) => asyncAction === undefined &&
                addAnotation(video!.id, newAnotation, authenticatedFetch, setAnotations, setAsyncAction, userProfilePicManager),
            edit: (anotation: Anotation, anotationUpdate: AnotationPublicFields) => asyncAction === undefined &&
                changeAnotation(anotation, anotationUpdate, authenticatedFetch, setAnotations, setAsyncAction),
            deleteAnotation: (anotation: Anotation) => asyncAction === undefined &&
                deleteAnotation(anotation, authenticatedFetch, setAnotations, setAsyncAction)
        },
        asyncAction: asyncAction
    };
}