import { Line } from "react-chartjs-2";
import {
    Chart as ChartJS,
    CategoryScale,
    LinearScale,
    PointElement,
    LineElement,
    Title,
    Tooltip,
    Legend,
    TimeScale,
    ChartOptions,
    TooltipItem,
    Tick,
} from "chart.js";
import { AuctionAnalysisDto } from "../../../../types/dto/response/auction";
import "chartjs-adapter-moment";

ChartJS.register(
    TimeScale,
    LinearScale,
    PointElement,
    LineElement,
    Title,
    Tooltip,
    Legend
);

function AuctionAnalysisGraph({
    title,
    status,
    endTime,
    bidHistoryGraph,
}: AuctionAnalysisDto) {
    const labels = bidHistoryGraph.map((bid) => bid.time);
    const dataPoints = bidHistoryGraph.map((bid) => bid.price);

    const data = {
        labels: labels,
        datasets: [
            {
                label: "입찰 가격",
                data: dataPoints,
                fill: true,
                backgroundColor: "rgba(75, 192, 192, 0.2)",
                borderColor: "rgba(75, 192, 192, 1)",
                tension: 0.1,
            },
        ],
    };

    const options: ChartOptions<'line'> = {
        responsive: true,
        plugins: {
            legend: {
                position: "top",
            },
            title: {
                display: true,
                text: "실시간 입찰 기록",
            },
        },
        scales: {
            x: {
                type: "time", // X축을 시간 축으로 설정
                time: {
                    unit: "minute",
                    tooltipFormat: "YYYY-MM-DD HH:mm:ss",
                    displayFormats: {
                        minute: "HH:mm",
                    },
                },
                title: {
                    display: true,
                    text: "입찰 시간",
                },
                max: endTime,
            },
            y: {
                beginAtZero: true,
                title: {
                    display: true,
                    text: "가격 (원)",
                },
                ticks: {
                    callback: function(
                        this: any, 
                        value: string | number,
                        index: number,
                        ticks: Tick[]
                    ): string | null | undefined {
                        if (typeof value === 'number') {
                            return value.toLocaleString();
                        }
                        return value;
                    }
                }
            },
        },
    };

    return <Line options={options} data={data} />;
}

export default AuctionAnalysisGraph;
