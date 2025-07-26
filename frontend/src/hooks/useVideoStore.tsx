import { Dispatch, SetStateAction, useEffect, useState } from "react"
import useAuthenticatedFetch from "@hooks/useAuthenticatedFetch"

export enum VisibilityType {
    PRIVATE = "PRIVATE",
    RESTRICTED = "RESTRICTED",
    PUBLIC = "PUBLIC"
}

export type VideoVisibility = {
    type: VisibilityType,
    canAnotateVisitants: {
        name: string,
        profilePic?: string
    }[] | null,
    canNotAnotateVisitants: {
        name: string,
        profilePic?: string
    }[] | null
}

export type Video = {
    id: string,
    ownerName: string,
    title: string,
    description: string,
    duration: number,
    visibility: VideoVisibility,
    thumb?: string,
    video?: string
}

export type NewVideo = {
    title: string,
    description: string,
    video: File
}

export enum SelectedPage {
    MEUS_VIDEOS,
    COMPARTILHADOS,
    PUBLICOS
}

type VideoStore = {
    get: (type: SelectedPage) => Video[],
    newVideo: (video: NewVideo) => void,
    isFetchingVideos: (type: SelectedPage) => boolean,
    isCreatingNewVideo: () => boolean
}

type PageVideoStores = {
    [key in SelectedPage]: Video[]
}

type FetchedPageVideos = {
    [key in SelectedPage]: number | undefined
}

function getFetchPath(type: SelectedPage) {
    switch (type) {
        case SelectedPage.MEUS_VIDEOS: return "self";
        case SelectedPage.COMPARTILHADOS: return "shared";
        case SelectedPage.PUBLICOS: return "public";
    }
}

async function fetchPageVideos(
    type: SelectedPage,
    authenticatedFetch: ReturnType<typeof useAuthenticatedFetch>,
    setIsLoading: Dispatch<SetStateAction<FetchedPageVideos>>,
    setPageVideos: Dispatch<SetStateAction<PageVideoStores>>
) {
    try {
        const response = await authenticatedFetch("http://localhost:8080/video/" + getFetchPath(type));

        if (!response.ok) {
            setIsLoading(cur => ({...cur, [type]: Date.now()}));
            return;
        }

        const results = await response.json() as Video[];

        const videosWithThumb = results.reduce<
            Record<Video["id"], Video>
        >(
            (acc, cur) => {
                acc[cur.id] = cur;
                return acc;
            },
            {}
        );

        await Promise.all(
            results.map(async v => {
                const response = await authenticatedFetch(`http://localhost:8080/video/${v.id}/thumb`);

                if (!response.ok)
                    return;

                const blob = await response.blob();

                videosWithThumb[v.id].thumb = URL.createObjectURL(blob);
            })
        );

        setPageVideos(cur => ({...cur, [type]: Object.values(videosWithThumb)}));
        setIsLoading(cur => ({...cur, [type]: Date.now()}));
    } catch {
        setIsLoading(cur => ({...cur, [type]: Date.now()}));
    }
}

function pasted30Minutes(date: number) {
    const SECONDS_PER_MILISECONDS = 1000;
    const MINUTES_TO_SECONDS = 60;
    return (Date.now() - date) / SECONDS_PER_MILISECONDS > 30 * MINUTES_TO_SECONDS;
}

async function newVideo(
    video: NewVideo,
    authenticatedFetch: ReturnType<typeof useAuthenticatedFetch>,
    setPageVideos: Dispatch<SetStateAction<PageVideoStores>>,
    setIsAdding: Dispatch<SetStateAction<boolean>>
) {
    try {
        setIsAdding(true);

        const response = await authenticatedFetch("http://localhost:8080/video", {
            method: "POST",
            body: Object.entries(video).reduce((acc, cur) => {
                acc.append(cur[0], (cur[1] as string | Blob));
                return acc;
            }, new FormData())
        });

        const result = await response.json();

        if (!response.ok) {
            alert((result as { error: string }).error);
            setIsAdding(false);
            return;
        }

        const thumbResponse = await authenticatedFetch(`http://localhost:8080/video/${result.id}/thumb`);

        if (thumbResponse.ok) {
            const thumbResult = await thumbResponse.blob();
            
            result.thumb = URL.createObjectURL(thumbResult);
        }

        setPageVideos(cur => ({
            ...cur,
            [SelectedPage.MEUS_VIDEOS]:
                [...cur[SelectedPage.MEUS_VIDEOS], result]
        }));

        setIsAdding(false);
    } catch {
        alert("Não foi possível adicionar um novo vídeo");
        setIsAdding(false);
    }
}

export default function useVideoStore(): VideoStore {
    const authenticatedFetch = useAuthenticatedFetch();

    const [ pageVideos, setPageVideos ] = useState<PageVideoStores>({
        [SelectedPage.MEUS_VIDEOS]: [],
        [SelectedPage.COMPARTILHADOS]: [],
        [SelectedPage.PUBLICOS]: []
    });
    const [ isLoading, setIsLoading ] = useState<FetchedPageVideos>({
        [SelectedPage.MEUS_VIDEOS]: undefined,
        [SelectedPage.COMPARTILHADOS]: undefined,
        [SelectedPage.PUBLICOS]: undefined
    });
    const [ isAdding, setIsAdding ] = useState(false);

    useEffect(() => {
        fetchPageVideos(SelectedPage.MEUS_VIDEOS, authenticatedFetch, setIsLoading, setPageVideos);
        fetchPageVideos(SelectedPage.COMPARTILHADOS, authenticatedFetch, setIsLoading, setPageVideos);
        fetchPageVideos(SelectedPage.PUBLICOS, authenticatedFetch, setIsLoading, setPageVideos);
    }, []);

    return {
        get: (type: SelectedPage) => {
            if (isLoading[type] !== undefined && pasted30Minutes(isLoading[type])) {
                fetchPageVideos(type, authenticatedFetch, setIsLoading, setPageVideos);
            }

            return pageVideos[type];
        },
        newVideo: async (video: NewVideo) => {
            return newVideo(video, authenticatedFetch, setPageVideos, setIsAdding);
        },
        isFetchingVideos: (type: SelectedPage) => {
            return isLoading[type] === undefined;
        },
        isCreatingNewVideo: () => {
            return isAdding;
        }
    };
}