import { AuctionAnalysisDto } from '../../../../types/dto/response/auction';
import { Line } from 'react-chartjs-2';
import {
  Chart,
  CategoryScale,
  LinearScale,
  PointElement,
  LineElement,
  Title,
  Tooltip,
  Legend,
  TimeScale,
} from 'chart.js';
Chart.register(
  CategoryScale,
  LinearScale,
  PointElement,
  LineElement,
  Title,
  Tooltip,
  Legend
);

interface Props {
  auctionHistory: AuctionAnalysisDto[];
  colors: string[];
}

interface ProcessedChartData {
  labels: number[];
  datasets: any[];
}

interface ChartDataPoint {
  elapsedMinutes: number;
  [key: string]: number;
}

const COLORS = ['#8884d8', '#82ca9d', '#ffc658', '#ff7300', '#387908', '#e6194B'];

function processChartData(history: AuctionAnalysisDto[], colors: string[]): ProcessedChartData {
    const allElapsedMinutes = new Set<number>([0]);
    const dataMap = new Map<number, { [key: string]: number | undefined }>();

    history.forEach(auction => {
      const startTimeMs = new Date(auction.startTime).getTime();

      let entryAtZero = dataMap.get(0) || {};
      entryAtZero[auction.title] = Math.max(entryAtZero[auction.title] || 0, auction.startPrice);
      dataMap.set(0, entryAtZero);

      auction.bidHistoryGraph.forEach(point => {
        const pointTimeMs = new Date(point.time).getTime();
        if (pointTimeMs < startTimeMs) return; 
        
        const elapsed = Math.floor((pointTimeMs - startTimeMs) / (1000 * 60));
        allElapsedMinutes.add(elapsed);

        let entry = dataMap.get(elapsed) || {};
        
        entry[auction.title] = Math.max(entry[auction.title] || 0, point.price);
        dataMap.set(elapsed, entry);
      });
    });

    const sortedMinutes = Array.from(allElapsedMinutes).sort((a, b) => a - b);

    const datasets = history.map((auction, index) => {
      const data: (number | null)[] = [];
      
      let localLastPrice = 0; 

      sortedMinutes.forEach(minute => {
        if (minute === 0) {
          localLastPrice = Math.max(localLastPrice, auction.startPrice);
        }

        const priceAtThisMinute = (dataMap.get(minute) || {})[auction.title];

        if (priceAtThisMinute !== undefined) {
          localLastPrice = Math.max(localLastPrice, priceAtThisMinute);
        }

        data.push(localLastPrice);
      });

      return {
        label: auction.title,
        data: data,
        borderColor: colors[index % colors.length],
        backgroundColor: colors[index % colors.length],
        fill: false,
        pointRadius: 1,
        stepped: true,
      };
    });

    return {
      labels: sortedMinutes,
      datasets: datasets,
    };
}

// options
const chartOptions: any = {
  responsive: true,
  maintainAspectRatio: false,
  interaction: {
    mode: 'index',
    intersect: false,
  },
  plugins: {
    legend: {
      position: 'top' as const,
    },
    tooltip: {
      callbacks: {
        label: function (context: any) {
          let label = context.dataset.label || '';
          if (label) {
            label += ': ';
          }
          if (context.parsed.y !== null) {
            label += `${context.parsed.y.toLocaleString()} 원`;
          }
          return label;
        },
      },
    },
  },
  scales: {
    x: {
      title: {
        display: true,
        text: '경과 시간 (분)',
      },
      ticks: {
        callback: (value: any) => `${value}분`,
      },
    },
    y: {
      title: {
        display: true,
        text: '가격 (원)',
      },
      ticks: {
        callback: (value: any) => {
          if (Number(value) >= 1000000) return `${Number(value) / 1000000}M`;
          if (Number(value) >= 1000) return `${Number(value) / 1000}K`;
          return value;
        },
      },
    },
  },
};

export default function AuctionAnalysisGraphs({ auctionHistory, colors}: Props) {
    const data = processChartData(auctionHistory, COLORS);

    return (
        <div className="chart-container">
          <Line options={chartOptions} data={data} />
        </div>
    );
}