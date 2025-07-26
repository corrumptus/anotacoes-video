"use client";

import { useRouter } from "next/navigation";
import { useState } from "react";
import AddVideo from "@components/user/AddVideo";
import SideNavBar from "@components/user/SideBar";
import VideoList from "@components/user/VideoList";
import useAuthenticationRedirection, { WhenRedirect } from "@hooks/useAuthenticationRedirection";
import useAuthenticationStorage from "@hooks/useAuthenticationStorage";
import useUsername from "@hooks/useUsername";
import useVideoStore, { SelectedPage } from "@hooks/useVideoStore";

export default function UserPage() {
    const router = useRouter();
    const username = useUsername();
    const [ isColapsed, setIsColapsed ] = useState(false);
    const [ selectedPage, setSelectedPage ] = useState(SelectedPage.MEUS_VIDEOS);
    const videoStore = useVideoStore();
    const [ isAddVisible, setIsAddVisible ] = useState(false);
    const authenticationStorage = useAuthenticationStorage();

    useAuthenticationRedirection(WhenRedirect.NON_AUTHENTICATED);
    
    return (
        <div className="h-full flex flex-col bg-[#ffffbd]">
            <header className="border-b border-black flex items-center justify-between px-1 text-2xl">
                <span>{username}</span>
                <button onClick={() => {
                    authenticationStorage.unset();

                    router.push("/");
                }}>
                    <img
                        src="https://cdn1.iconfinder.com/data/icons/heroicons-ui/24/logout-512.png"
                        alt="sair(uma seta apontando para o outro lado de uma porta)"
                        className="w-[40px] h-[40px] cursor-pointer"
                    />
                </button>
            </header>
            <main className="h-full flex overflow-hidden">
                <SideNavBar
                    selectedPage={selectedPage}
                    changeSelectedPage={setSelectedPage}
                    isColapsed={isColapsed}
                    switchColapse={() => setIsColapsed(cur => !cur)}
                />
                <section className="h-full w-full flex flex-col overflow-hidden bg-lime-400">
                    <header className="flex flex-row-reverse p-2">
                        <button
                            className={`${selectedPage === SelectedPage.MEUS_VIDEOS ? "opacity-100 cursor-pointer" : "opacity-0"}`}
                            onClick={() => setIsAddVisible(true)}
                        >
                            <img
                                src="https://images.icon-icons.com/902/PNG/512/plus_icon-icons.com_69322.png"
                                alt="adicionar novo vídeo(mais)"
                                className="w-[40px] h-[40px]"
                            />
                        </button>
                    </header>
                    <VideoList
                        isLoading={videoStore.isFetchingVideos(selectedPage)}
                        videos={videoStore.get(selectedPage)}
                    />
                </section>
                <AddVideo
                    isVisible={isAddVisible}
                    close={() => setIsAddVisible(false)}
                    add={videoStore.newVideo}
                    isAdding={videoStore.isCreatingNewVideo()}
                />
            </main>
        </div>
    );
}