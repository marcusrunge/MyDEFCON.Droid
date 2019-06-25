using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

using Android.App;
using Android.App.Job;
using Android.Content;
using Android.OS;
using Android.Runtime;
using Android.Views;
using Android.Widget;
using JobSchedulerType = Android.App.Job.JobScheduler;

namespace MyDEFCON.Services
{
    public interface IJobService
    {
        void CreateJob<T>(int jobId, long minimumLatency, bool recurring) where T : JobService;
        void CancelJob(int JobId);
        void CancelAllJobs();
    }
    public class JobManagementService : IJobService
    {
        Context _context;
        JobScheduler _jobScheduler;
        public JobManagementService(Context context)
        {
            _context = context;
            _jobScheduler = (JobSchedulerType)_context.GetSystemService(Context.JobSchedulerService);
        }
        public void CancelAllJobs()
        {
            _jobScheduler.CancelAll();
        }

        public void CancelJob(int jobId)
        {
            _jobScheduler.Cancel(jobId);
        }

        public void CreateJob<T>(int jobId, long minimumLatency, bool recurring) where T : JobService
        {
            var jobBuilder = CreateJobBuilderUsingJobId<T>(jobId);
            var jobInfo = jobBuilder.Build();
        }

        private JobInfo.Builder CreateJobBuilderUsingJobId<T>(int jobId) where T : JobService
        {
            var javaClass = Java.Lang.Class.FromType(typeof(T));
            var componentName = new ComponentName(_context, javaClass);
            return new JobInfo.Builder(jobId, componentName);
        }
    }
}