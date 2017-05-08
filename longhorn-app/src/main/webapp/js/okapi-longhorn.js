/*===========================================================================
  Copyright (C) 2011-2017 by the Okapi Framework contributors
-----------------------------------------------------------------------------
  Licensed under the Apache License, Version 2.0 (the "License");
  you may not use this file except in compliance with the License.
  You may obtain a copy of the License at

  http://www.apache.org/licenses/LICENSE-2.0

  Unless required by applicable law or agreed to in writing, software
  distributed under the License is distributed on an "AS IS" BASIS,
  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  See the License for the specific language governing permissions and
  limitations under the License.
===========================================================================*/

   var serviceUrl = window.location.pathname;
   var projectsUrl = serviceUrl + "projects/";

   //load or refresh the project list
   function loadProjectList(){
   		$.get(projectsUrl, function(data) {
			$("#projects").empty();
			$(data).find("e").each(function(){
				$("#projects").append("<tr><td><a class=\"withicon\" OnClick=\"loadProjectDetails('"+$(this).text()+"');\" href=\"#\">Project: "+$(this).text()+" <img src=\"icons/zoom.png\"></a></td><td><a OnClick=\"deleteProject('"+$(this).text()+"');\" href=\"#\"><img src=\"icons/cancel.png\"></a></tr> ");
			});
		}
		,"xml");   
	};

	//load project details
	function loadProjectDetails(prjid){

		countInputFiles = 0;
		countOutputFiles = 0;
	
		$("#project").empty().append(prjid);
		$("#toggle_details").show();
		$("#files").hide();
		
   		$.get(projectsUrl+prjid+"/inputFiles", function(data) {
			$("#input_files").empty();
			$(data).find("e").each(function(){
				$("#input_files").append("<tr><td><a href=\""+projectsUrl+prjid+"/inputFiles/"+$(this).text()+"\" target=\"_blank\">"+$(this).text()+"</a></td></tr>");
				countInputFiles=countInputFiles+1;
			});
			if(countInputFiles > 0){
			   $("#files").show();
			}
		}
		,"xml");   
   
   		$.get(projectsUrl+prjid+"/outputFiles", function(data) {
			$("#output_files").empty();
			$(data).find("e").each(function(){
				$("#output_files").append("<tr><td><a href=\""+projectsUrl+prjid+"/outputFiles/"+$(this).text()+"\" target=\"_blank\">"+$(this).text()+"</a></td></tr>");
				countOutputFiles=countOutputFiles+1;
			});
			if(countOutputFiles > 0){
			   $("#files").show();
			}
		}
		,"xml");   
		
	};

	//delete a project
	function deleteProject(id){
   
		$.ajax({
			type: "DELETE",
			url: projectsUrl+id,
			complete: function(msg){
				loadProjectList();
			}
		});
		
		if($("#project").text() == id){
		 $("#toggle_details").hide();
		}
		
    };
	
	function executeProject(){
   
		$.ajax({
			type: "POST",
			url: projectsUrl+$("#project").text()+"/tasks/execute",
			success: function() { 
				alert("Project "+$("#project").text()+" executed");
				loadProjectDetails($("#project").text());
			}, 
			error: function() { 
				alert("Project "+$("#project").text()+" execution failed. Please check batch config and input files.");
			}, 
			dataType: "xml"
		});
   
    };

    function toggleXMLStepParamOverride(cb) {
    	if(cb.checked)
    		$("#overrideStepParams").show();
    	else
    		$("#overrideStepParams").hide();
    }
    
$(document).ready(function() {

   $("#toggle_details").hide();

   $("#list_projects").click(function() {
   	 loadProjectList();
   });
   
   $("#create_project").click(function() {
		$.post(projectsUrl+"new/", function(data) {
			loadProjectList();	 
		}
		,"xml");  
   });
   
   $("#batchForm").ajaxForm({ 
		beforeSubmit:  function(formData, jqForm, options) { 
			options.url=projectsUrl+$("#project").text()+"/batchConfiguration";
		},
		success:    function(responseText, statusText) {
			alert("BatchConfiguration File Uploaded!, statusText="+statusText+", responseText="+responseText); 
			loadProjectDetails($("#project").text());
		} 
	}); 
	
	$("#inputFileForm").ajaxForm({ 
		beforeSubmit:  function(formData, jqForm, options) {

			//--Handles browser incompatibility in dealing with the path--
		    var fileAndPath = $("#inputFileForm :file").fieldValue()[0];
		    var lastPathDelimiter = fileAndPath.lastIndexOf("\\");
		    var lastPathDelimiterForward = fileAndPath.lastIndexOf("/");
		    var fileNameOnly;
		    
		    if (lastPathDelimiter != -1){
		        fileNameOnly = fileAndPath.substring(lastPathDelimiter+1);
		    }else if (lastPathDelimiterForward != -1){
		    	fileNameOnly = fileAndPath.substring(lastPathDelimiterForward+1);
		    }else{
		    	fileNameOnly = fileAndPath;
		    }
		
			options.url=projectsUrl+$("#project").text()+"/inputFiles/"+fileNameOnly;
		},
		success:    function(responseText, statusText, xhr, $form) { 

			alert("Input File Uploaded!"); 
			loadProjectDetails($("#project").text());
		} 
	}); 
});



