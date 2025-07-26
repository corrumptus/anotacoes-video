import { forwardRef, Ref, useState } from "react";
import { Video } from "@hooks/useVideoStore";

const VideoSection = forwardRef((
    {
        video,
        videoInstantsSlice,
        setAnotation
    }: {
        video: Video | undefined,
        videoInstantsSlice: number[],
        setAnotation: (index: number) => void
    },
    ref: Ref<HTMLVideoElement>
) => {
    const [ isWaiting, setIsWaiting ] = useState(false);
    const [ timeoutId, setTimeoutId ] = useState<number>();

    function onPlay(curTime: number) {
        const nextIndex = videoInstantsSlice.findIndex(vi => vi >= curTime);

        if (nextIndex === -1)
            return;

        setTimeoutId(
            setTimeout(() => {
                setAnotation(nextIndex);

                onPlay(videoInstantsSlice[nextIndex]);
            }, (videoInstantsSlice[nextIndex] - curTime) * 1000, undefined)
        );
    }

    function onPause() {
        clearTimeout(timeoutId);
        setTimeoutId(undefined);
    }

    if (video === undefined) return (
        <section className="h-full w-[70%] flex items-center justify-center">
            Vazio
        </section>
    );

    return (
        <section className="h-full p-10 w-[70%] overflow-auto">
            <header className="font-bold text-xl underline decoration-red-600">
                {video.title}
            </header>
            <div>
                <video
                    src={video.video}
                    controls
                    ref={ref}
                    onPlay={e => onPlay((e.target as HTMLVideoElement).currentTime)}
                    onPause={onPause}
                    onEnded={onPause}
                    onPlaying={e => {
                        if (!isWaiting)
                            return;

                        onPlay((e.target as HTMLVideoElement).currentTime);
                        setIsWaiting(false);
                    }}
                    onSeeking={onPause}
                    onSeeked={e => onPlay((e.target as HTMLVideoElement).currentTime)}
                    onWaiting={() => setIsWaiting(true)}
                />
            </div>
            <div className="rounded-2xl bg-gray-400 mt-4 p-3 underline decoration-red-600">
                {video.description}
            </div>
        </section>
    );
});

VideoSection.displayName = "VideoSection";

export default VideoSection;