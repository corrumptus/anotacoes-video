"use client";

import { useParams, useRouter } from "next/navigation";
import { useEffect, useRef, useState } from "react";
import AddAnotation from "@components/video/AddAnotation";
import Anotations from "@components/video/Anotations";
import EditAnotation from "@components/video/EditAnotation";
import VideoOptions from "@components/video/VideoOptions";
import VideoSection from "@components/video/VideoSection";
import useAuthenticationRedirection, { WhenRedirect } from "@hooks/useAuthenticationRedirection";
import useUsername from "@hooks/useUsername";
import useVideoAndAnotations, { Anotation, AnotationPublicFields } from "@hooks/useVideoAndAnotations";

export default function VideoPage() {
    const { id } = useParams<{ [key in string]: string }>();
    const router = useRouter();
    const videoRef = useRef<HTMLVideoElement>(null);
    const [ videoCurrentTime, setVideoCurrentTime ] = useState(0);
    const [ videoSinkedAnotations, setVideoSinkedAnotation ] = useState<number>(0);
    const [ isVideoOptionsVisible, setIsVideoOptionsVisible ] = useState(false);
    const [ isNewAnotationVisible, setIsNewAnotationVisible ] = useState(false);
    const [ anotation, setAnotation ] = useState<Anotation | undefined>(undefined);
    const manager = useVideoAndAnotations(id);
    const userName = useUsername();

    useAuthenticationRedirection(WhenRedirect.NON_AUTHENTICATED);
  
    useEffect(() => {
        setVideoCurrentTime(videoRef.current?.currentTime as number);
    }, [isNewAnotationVisible]);

    function modalVisibility(modal: "vo" | "aa", to: boolean) {
        setIsVideoOptionsVisible(modal === "vo" ? to : false);
        setIsNewAnotationVisible(modal === "aa" ? to : false);
        setAnotation(undefined);
    }

    if (
        manager.asyncAction === "video-loading"
        ||
        manager.asyncAction === "anotation-loading"
    ) {
        return (
            <div className="h-full flex flex-col bg-[#ffffbd]">
                <header className="flex justify-between border-b border-black h-[48px] p-1">
                    <button onClick={() => router.push("/user")}>
                        <img
                            src="https://static.vecteezy.com/system/resources/thumbnails/019/858/315/small_2x/back-flat-color-outline-icon-free-png.png"
                            alt="voltar(flecha para a esquerda)"
                            className="w-[40px] h-[40px] cursor-pointer"
                        />
                    </button>
                </header>
                <main className="h-full flex items-center justify-center">
                    Loading...
                </main>
            </div>
        );
    }

    if (manager.video.video === undefined) {
        return (
            <div className="h-full flex flex-col bg-[#ffffbd]">
                <header className="flex justify-between border-b border-black h-[48px] p-1">
                    <button onClick={() => router.push("/user")}>
                        <img
                            src="https://static.vecteezy.com/system/resources/thumbnails/019/858/315/small_2x/back-flat-color-outline-icon-free-png.png"
                            alt="voltar(flecha para a esquerda)"
                            className="w-[40px] h-[40px] cursor-pointer"
                        />
                    </button>
                </header>
                <main className="h-full flex items-center justify-center">
                    Vazio
                </main>
            </div>
        );
    }

    return (
        <div className="h-full flex flex-col bg-[#ffffbd]">
            <header className="flex justify-between border-b border-black h-[48px] p-1">
                <button onClick={() => router.push("/user")}>
                    <img
                        src="https://static.vecteezy.com/system/resources/thumbnails/019/858/315/small_2x/back-flat-color-outline-icon-free-png.png"
                        alt="voltar(flecha para a esquerda)"
                        className="w-[40px] h-[40px] cursor-pointer"
                    />
                </button>
                {userName === manager.video.video!.ownerName &&
                    <button
                        onClick={() => modalVisibility("vo", true)}
                    >
                        <img
                            src="https://www.freeiconspng.com/uploads/info-icon-32.png"
                            alt="informações do vídeo(i)"
                            className="w-[40px] h-[40px] cursor-pointer"
                        />
                    </button>
                }
            </header>
            <main className="h-full flex overflow-hidden">
                <VideoSection
                    video={manager.video.video}
                    videoInstantsSlice={manager.anotations.anotations.reduce<number[]>(
                        (acc, cur, i) => {
                            if (i < videoSinkedAnotations)
                                return acc;

                            acc.push(cur.videoInstant);

                            return acc;
                        }, []
                    )}
                    setAnotation={(index: number) => setVideoSinkedAnotation(index)}
                    ref={videoRef}
                />
                <section className=" w-[30%] flex flex-col border-l border-black">
                    {(
                        userName === manager.video.video!.ownerName
                        ||
                        (
                            manager.video.video!.visibility.canAnotateVisitants !== null
                            &&
                            manager.video.video!.visibility.canAnotateVisitants.find(
                            v => v.name === userName
                            ) !== undefined
                        )
                    ) &&
                        <div className="h-[48px] flex flex-row-reverse border-b border-black p-1">
                            <button
                                onClick={() => modalVisibility("aa", true)}
                            >
                                <img
                                    src="https://images.icon-icons.com/902/PNG/512/plus_icon-icons.com_69322.png"
                                    alt="adicionar uma anotação(mais)"
                                    className="w-[40px] h-[40px] cursor-pointer"
                                />
                            </button>
                        </div>
                    }
                    <Anotations
                        anotations={manager.anotations.anotations}
                        currentAnotation={videoSinkedAnotations}
                        setCurrentTime={(time: number) => {videoRef.current!.currentTime = time}}
                        openAnotation={(anotation: Anotation) => setAnotation(anotation)}
                        deleteAnotation={manager.anotations.deleteAnotation}
                        isOwner={userName === manager.video.video!.ownerName}
                    />
                </section>
            </main>
            <VideoOptions
                manager={manager.video}
                asyncAction={manager.asyncAction}
                isVisible={isVideoOptionsVisible}
                close={() => modalVisibility("vo", false)}
            />
            <AddAnotation
                currentInstant={videoCurrentTime}
                maxTime={manager.video.video!.duration}
                isVisible={isNewAnotationVisible}
                close={() => modalVisibility("aa", false)}
                add={manager.anotations.addAnotation}
                isAdding={manager.asyncAction === "anotation-add"}
            />
            <EditAnotation
                maxTime={manager.video.video!.duration}
                anotation={anotation}
                edit={(anotationChange: AnotationPublicFields) => manager.anotations.edit(anotation!, anotationChange)}
                isEditing={manager.asyncAction === "anotation-edit"}
                close={() => setAnotation(undefined)}
            />
        </div>
    );
}