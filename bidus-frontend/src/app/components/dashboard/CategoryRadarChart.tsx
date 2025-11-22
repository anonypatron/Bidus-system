import React from 'react';
import {
  Chart as ChartJS,
  RadialLinearScale,
  PointElement,
  LineElement,
  TooltipItem,
  Filler,
  Tooltip,
  Legend,
  ChartOptions,
} from 'chart.js';
import { Radar } from 'react-chartjs-2';
import { CategoryStatsDto } from '../../../../types/dto/response/dashboard';

ChartJS.register(
  RadialLinearScale,
  PointElement,
  LineElement,
  Filler,
  Tooltip,
  Legend
);

interface Props {
  categories: CategoryStatsDto[];
}

const CategoryRadarChart: React.FC<Props> = ({ categories }) => {
    const data = {
        labels: categories.map((c) => c.categoryName),
        datasets: [
            {
                label: '낙찰 횟수',
                data: categories.map((c) => c.count),
                backgroundColor: 'rgba(176, 137, 104, 0.2)', // --accent-primary의 20%
                borderColor: 'rgba(176, 137, 104, 1)',     // --accent-primary
                borderWidth: 2,
                pointBackgroundColor: 'rgba(176, 137, 104, 1)',
                pointBorderColor: '#fff',
                pointHoverBackgroundColor: '#fff',
                pointHoverBorderColor: 'rgba(176, 137, 104, 1)',
            },
        ],
    };

    const options: ChartOptions<'radar'> = {
        responsive: true,
        maintainAspectRatio: false,
        plugins: {
            legend: {
                position: 'top',
            },
            tooltip: {
                callbacks: {
                    label: (context: TooltipItem<'radar'>) => {
                        return `${context.dataset.label}: ${context.raw}회`;
                    },
                },
            },
        },
        scales: {
            r: {
                min: 0,
                angleLines: {
                    color: 'rgba(0, 0, 0, 0.05)',
                },
                grid: {
                    color: 'rgba(0, 0, 0, 0.05)',
                },
                pointLabels: {
                    font: {
                        size: 13,
                        weight: 500,
                    },
                    color: '#5D503F',
                },
                ticks: {
                    backdropColor: 'transparent',
                    color: '#8E806A',
                    stepSize: 1,
                },
            },
        },
    };

    return (
        <div className="dashboard-card chart-card">
            <h2 className="card-title">카테고리 분포(Top {categories.length})</h2>
            <div style={{ position: 'relative', flexGrow: 1 }}>
                <Radar data={data} options={options} />
            </div>
        </div>
    );
};

export default CategoryRadarChart;