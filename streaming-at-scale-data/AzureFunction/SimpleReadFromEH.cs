using Microsoft.Azure.EventHubs;
using Microsoft.Azure.WebJobs;
using Microsoft.Azure.WebJobs.Host;
using Microsoft.Extensions.Logging;
using System;

namespace ReadEventHubs
{
    public static class SimpleReadFromEH
    {
        [Disable]
        [FunctionName("SimpleReadFromEH")]
        public static void Run([EventHubTrigger("%EventHubName%", Connection = "EventHubConnectionAppSetting", ConsumerGroup = "%ConsumerGroupRead%")]EventData[] myEventHubMessage, ILogger log)
        {
            log.LogInformation("Event Hub batch size - " + myEventHubMessage.Length.ToString());
            //log.LogInformation($"C# Event Hub trigger function processed a message: {myEventHubMessage} at {DateTime.Now}");
        }
    }
}
