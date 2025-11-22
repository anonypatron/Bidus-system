import {
    Chart as ChartJS,
    ChartOptions,
    Legend,
    LinearScale,
    LineElement,
    PointElement,
    Tick,
    TimeScale,
    Title,
    Tooltip,
    TooltipItem,
    CategoryScale
} from "chart.js";
import "chartjs-adapter-moment";
import { Line } from "react-chartjs-2";
import { AuctionAnalysisDto } from "../../../../types/dto/response/auction";

ChartJS.register(
    TimeScale,
    LinearScale,
    CategoryScale,
    PointElement,
    LineElement,
    Title,
    Tooltip,
    Legend
);

function formatTime(isoString: string): string {
    return new Date(isoString).toLocaleString('ko-KR', {
        month: '2-digit',
        day: '2-digit',
        hour: '2-digit',
        minute: '2-digit',
    });
}

function AuctionAnalysisGraph({
    title,
    startTime,
    startPrice,
    bidHistoryGraph,
}: AuctionAnalysisDto) {
    const labels = [
        "경매 시작",
        ...bidHistoryGraph.map((bid, index) => `입찰 #${index + 1}`)
    ];
    const dataPoints = [
        startPrice,
        ...bidHistoryGraph.map((bid) => bid.price)
    ];

    const data = {
        labels: labels,
        datasets: [
            {
                label: "입찰 가격",
                data: dataPoints,
                fill: true,
                backgroundColor: "rgba(74, 92, 106, 0.2)", // --accent-blue-dark (20%)
                borderColor: "#4A5C6A",                   // --accent-blue-dark
                
                tension: 0.1,
                pointRadius: 4,
                pointHoverRadius: 7,
                
                /* 🎨 [수정] 포인트 색상도 테마에 맞춤 */
                pointBackgroundColor: "#4A5C6A",
                pointBorderColor: "#FFFFFF",
                pointHoverBackgroundColor: "#FFFFFF",
                pointHoverBorderColor: "#4A5C6A",
            },
        ],
    };

    const options: ChartOptions<'line'> = {
        responsive: true,
        maintainAspectRatio: false,
        plugins: {
            legend: {
                position: "top",
            },
            title: {
                display: true,
                text: `"${title}" 입찰 기록`,
                font: { size: 16 }
            },
            tooltip: {
                callbacks: {
                    title: function(tooltipItems: TooltipItem<'line'>[]) {
                        const label = tooltipItems[0].label;
                        let timeString = "";
                        
                        if (label === "경매 시작") {
                            timeString = formatTime(startTime);
                        } else {
                            const bidIndex = parseInt(label.split('#')[1]) - 1;
                            if (bidHistoryGraph[bidIndex]) {
                                timeString = formatTime(bidHistoryGraph[bidIndex].time);
                            }
                        }
                        return `${label} (${timeString})`;
                    },
                    label: function(context) {
                        let label = context.dataset.label || '';
                        if (label) {
                            label += ': ';
                        }
                        if (context.parsed.y !== null) {
                            label += context.parsed.y.toLocaleString() + '원';
                        }
                        return label;
                    }
                }
            }
        },
        scales: {
            x: {
                // [수정] X축 타입을 "category"로 변경
                type: "category",
                title: {
                    display: true,
                    text: "입찰 순서",
                    font: { size: 14 }
                },
            },
            y: {
                // [수정] beginAtZero: false (삭제)
                // Y축이 0부터 시작하면 가격 변화가 잘 안보이므로,
                // Chart.js가 자동으로 최소/최대값을 잡도록 합니다.
                title: {
                    display: true,
                    text: "가격 (원)",
                    font: { size: 14 }
                },
                ticks: {
                    // Y축 눈금에 "10,000"처럼 콤마(,)를 추가
                    callback: function(value) {
                        if (typeof value === 'number') {
                            return value.toLocaleString();
                        }
                        return value;
                    }
                }
            },
        },
    };

    // [추가] 입찰 기록이 없는 경우 (시작점만 있는 경우)
    if (dataPoints.length <= 1) {
        return (
            <div className="graph-placeholder-container">
                <p className="graph-placeholder-text">
                    아직 입찰 기록이 없습니다.
                </p>
            </div>
        )
    }

    return <Line options={options} data={data} />;
}

export default AuctionAnalysisGraph;
